package com.example.duckhunt

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class LoginActivity : AppCompatActivity() {
    private lateinit var etNick: EditText
    private lateinit var btnStart: Button
    private lateinit var nick:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        etNick = findViewById(R.id.etNick)
        btnStart = findViewById(R.id.btnStart)
        //val typeface = Typeface.createFromAsset(assets, "lobster.ttf")
        //etNick.typeface = typeface
        //btnStart.typeface = typeface
        btnStart.setOnClickListener {
            nick = etNick.text.toString()
            if(nick.isEmpty()) {
                etNick.error = "El nombre de usuario es obligatorio"
            } else if(nick.length < 3) {
                etNick.error = "Debe tener al menos tres caracteres"
            } else {
                val intent = Intent(this@LoginActivity, GameActivity::class.java)
                intent.putExtra(Constantes.EXTRA_NICK, nick)
                startActivity(intent)
            }
        }
    }
}