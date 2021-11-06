package rb.example.healthify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import rb.example.healthify.databinding.ActivityFinishBinding

class FinishActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFinishBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarFinishActivity)
        val actionBar = supportActionBar // actionbar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true) // set back button
        }
        binding.toolbarFinishActivity.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.btnFinish.setOnClickListener{
            finish() // Finishes the current activity and goes to start activity
        }
    }
}