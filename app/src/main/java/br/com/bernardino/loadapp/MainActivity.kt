package br.com.bernardino.loadapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.bernardino.loadapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var loadingButton: LoadingButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        loadingButton = binding.contentInc.customButton
        loadingButton.setOnClickListener {
            Toast.makeText(this, "File is downloading", Toast.LENGTH_LONG).show()
        }

        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)
    }
}