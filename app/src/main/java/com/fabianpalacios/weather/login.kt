package com.fabianpalacios.weather

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        //Setup
        setup()
    }
    private fun setup() {
        title = "Autenticación"

        // boton ingresar y compruebo que no esten vacios las sendas email y contraseña
        signUpbutton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(emailEditText.text.toString(),
                     passwordEditText.text.toString()).addOnCompleteListener {
                     if (it.isSuccessful){
                         showHome(it.result?.user?.email?:"", ProviderType.BASIC)

                     }else {
                         showAlert()
                     }
                  }
        }
        }

     logInButton.setOnClickListener {
         if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {

             FirebaseAuth.getInstance()
                 .signInWithEmailAndPassword(emailEditText.text.toString(),
                 passwordEditText.text.toString()).addOnCompleteListener {
                 if (it.isSuccessful){
                     showHome(it.result?.user?.email?:"", ProviderType.BASIC)

                 }else {
                     showAlert()
                 }
             }
         }
     }

    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showHome(email: String, provider: ProviderType){

        val homeIntent: Intent = Intent(this,MainActivity::class.java).apply{
            putExtra("email",email)
            putExtra("provider",provider.name)
        }
        startActivity(homeIntent)
    }

}
