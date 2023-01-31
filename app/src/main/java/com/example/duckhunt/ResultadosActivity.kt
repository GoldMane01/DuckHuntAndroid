package com.example.duckhunt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.INFO
import android.widget.Button
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class ResultadosActivity : AppCompatActivity() {

    private lateinit var tvTop1: TextView
    private lateinit var tvTop2: TextView
    private lateinit var tvTop3: TextView
    private lateinit var tvTop4: TextView
    private lateinit var tvTop5: TextView
    private var listaTop5 = mutableListOf<Usuario>()
    private lateinit var btnReinicio: Button
    private lateinit var btnSalir: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultados)
        tvTop1 = findViewById(R.id.tvTop1)
        tvTop2 = findViewById(R.id.tvTop2)
        tvTop3 = findViewById(R.id.tvTop3)
        tvTop4 = findViewById(R.id.tvTop4)
        tvTop5 = findViewById(R.id.tvTop5)

        btnReinicio = findViewById<Button>(R.id.btnReiniciar)
        btnSalir = findViewById<Button>(R.id.btnSalir)

        btnReinicio.setOnClickListener {
            setResult(RESULT_OK, intent)
            finish()
        }
        btnSalir.setOnClickListener {
            finish()
        }

        var nombre:String=""
        var puntos:Long=0
        var usuario:Usuario
        val db = FirebaseFirestore.getInstance()
        db.collection("puntuaciones").get().addOnSuccessListener { result ->
            for(document in result) {
                puntos = document.data.get("puntos") as Long
                nombre = document.id
                usuario = Usuario(nombre, puntos)
                listaTop5.add(usuario)
                Log.i("FIRE", "nombre:$nombre -> puntos:$puntos")
            }
            ordenarLista()
        }.addOnFailureListener { error ->
            Log.e("FirebaseError", error.message.toString())
        }
    }

    private fun ordenarLista() {

        var vueltas = 0
        while (vueltas < 7){
            for (i in 0..6) {
                if(listaTop5[i].puntos < listaTop5[i+1].puntos) {
                    var aux = listaTop5[i]
                    listaTop5[i] = listaTop5[i+1]
                    listaTop5[i+1] = aux
                }
            }
            vueltas++
        }
        tvTop1.text = "${listaTop5[0].nombre} -> ${listaTop5[0].puntos}"
        tvTop2.text = "${listaTop5[1].nombre} -> ${listaTop5[1].puntos}"
        tvTop3.text = "${listaTop5[2].nombre} -> ${listaTop5[2].puntos}"
        tvTop4.text = "${listaTop5[3].nombre} -> ${listaTop5[3].puntos}"
        tvTop5.text = "${listaTop5[4].nombre} -> ${listaTop5[4].puntos}"
    }
}