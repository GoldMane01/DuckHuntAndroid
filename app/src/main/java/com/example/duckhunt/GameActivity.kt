package com.example.duckhunt

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.TextView
import java.util.*
import android.os.Handler
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore

class GameActivity : AppCompatActivity() {
    private lateinit var tvCounter:TextView
    private lateinit var tvTimer:TextView
    private lateinit var tvNick:TextView
    private lateinit var nick:String
    private lateinit var ivPato:ImageView
    private var cazados:Long=0
    private var anchoPantalla=0
    private var altoPantalla=0
    private var gameOver=false
    private var aleatorio:Random = Random()
    private var listaUsuarios = mutableListOf<Usuario>()
    private var intentLaunch: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        initPantalla()
        inicializarComponentesVisuales()
        eventos()
        moverPato()
        initCuentaAtras()

        intentLaunch = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if(result.resultCode == RESULT_OK) {
                cazados = 0
                tvCounter.text = "0"
                gameOver = false
                initCuentaAtras()
                moverPato()
            } else {
                finish()
            }
        }

    }

    private fun inicializarComponentesVisuales() {
        tvCounter = findViewById(R.id.tvCounter)
        tvTimer = findViewById(R.id.tvTimer)
        tvNick = findViewById(R.id.tvNick)
        ivPato = findViewById(R.id.ivPato)

        //val typeface = Typeface.createFromAsset(assets, "lobster.ttf")
        //tvCounter.typeface = typeface
        //tvTimer.typeface = typeface
        //tvNick.typeface = typeface

        val extras = intent.extras
        nick = extras!!.getString(Constantes.EXTRA_NICK)!!
        tvNick.text = nick
    }

    private fun eventos() {
        ivPato.setOnClickListener() {
            if(!gameOver) {
                cazados++
                tvCounter.setText(cazados.toString())
                ivPato.setImageResource(R.drawable.duck_clicked)
                Handler().postDelayed({
                    ivPato.setImageResource(R.drawable.duck)
                    moverPato()
                }, 500)
            }
        }
    }

    private fun initPantalla() {
        val dm1 = resources.displayMetrics
        anchoPantalla = dm1.widthPixels
        altoPantalla = dm1.heightPixels
    }

    private fun moverPato() {
        val min = 0
        val maximoX = anchoPantalla - ivPato.width
        val maximoY = altoPantalla - ivPato.height
        val randomX = aleatorio.nextInt(maximoX - min + 1 + min)
        val randomY = aleatorio.nextInt(maximoY - min + 1 + min)
        ivPato.x = randomX.toFloat()
        ivPato.y = randomY.toFloat()
    }

    private fun initCuentaAtras() {
        object:CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val segundosRestantes = millisUntilFinished / 1000
                tvTimer.text = segundosRestantes.toString() + "s"
            }

            override fun onFinish() {
                tvTimer.text = "0s"
                gameOver = true
                transferirDatos()
                val intent = Intent(this@GameActivity, ResultadosActivity::class.java)
                intentLaunch?.launch(intent)
            }
        }.start()
    }

    private fun mostrarDialogoGameOver() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Has cazado $cazados patos").setTitle("Game Over")
        builder.setCancelable(false)
        builder.setPositiveButton(
            R.string.reiniciar
        ) { dialgo, which ->
            cazados = 0
            tvCounter.text = "0"
            gameOver = false
            initCuentaAtras()
            moverPato()
        }
        builder.setNegativeButton(
            R.string.salir
        ) { dialog, which ->
            dialog.dismiss()
            finish()
        }
        val dialogo = builder.create()
        dialogo.show()
        transferirDatos()
    }



    private fun actualizarDatos(usuario:Usuario) {
        val db = FirebaseFirestore.getInstance()
        db.collection("puntuaciones").document(usuario.nombre).update("puntos",usuario.puntos).addOnSuccessListener { result ->
            Log.i("Firebase","Datos actualizados correctamente")
        }.addOnFailureListener { error ->
            Log.e("FirebaseError",error.message.toString())
        }
    }

    private fun insertarDatos(usuario: Usuario) {
        val db = FirebaseFirestore.getInstance()
        val datos = hashMapOf("puntos" to usuario.puntos)
        db.collection("puntuaciones").document(usuario.nombre).set(datos).addOnSuccessListener { result ->
            Log.i("Firebase","Datos insertados correctamente")
        }.addOnFailureListener { error ->
            Log.e("FirebaseError",error.message.toString())
        }
    }

    private fun transferirDatos() {
        var actualizar = false
        var insertar = true
        if(listaUsuarios.isNotEmpty()) {
            for(usuario in listaUsuarios) {
                if(usuario.nombre.equals(nick)) {
                    insertar = false
                    if(usuario.puntos > cazados) {
                        actualizar = true
                    }
                }
            }
        }
        val jugador = Usuario(nick, cazados)
        if(actualizar) {
            actualizarDatos(jugador)
        }
        if(insertar) {
            insertarDatos(jugador)
        }
    }

}