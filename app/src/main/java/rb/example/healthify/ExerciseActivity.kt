package rb.example.healthify

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import rb.example.healthify.databinding.ActivityExerciseBinding
import rb.example.healthify.databinding.DialogCustomBackConfirmationBinding
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(),TextToSpeech.OnInitListener {
    private var restTimer:CountDownTimer?=null
    private var restProgress = 0

    private var exerciseTimer:CountDownTimer?=null
    private var exerciseProgress = 0
    //all the ExerciseModels need to be added here to display in tha app

    private var exerciseList: ArrayList<ExerciseModel>?= null
    private var currentExercisePosition = -1
    //private var selectedExercisePosition = -1
    private var tts:TextToSpeech?=null
    private var player:MediaPlayer?=null
    private var exerciseAdapter:ExerciseStatusAdapter?=null


    private lateinit var binding: ActivityExerciseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarExerciseActivity)
        val actionbar = supportActionBar
        if(actionbar!= null){
            actionbar.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbarExerciseActivity.setNavigationOnClickListener {
            //onBackPressed()
            customDialogForBackButton()
        }
        tts = TextToSpeech(this,this)

        exerciseList = Constants.defaultExerciseList()
        setupRestView()
        setUpExerciseStatusRecyclerView()
    }

    override fun onDestroy() {
        if(restTimer!=null){
            restTimer!!.cancel()
            restProgress =0
        }

        if(exerciseTimer!=null){
            exerciseTimer!!.cancel()
            exerciseProgress =0
        }

        if(tts!=null){
            tts!!.stop()
            tts!!.shutdown()
        }

        if(player!=null){
            player!!.stop()
        }
        super.onDestroy()
    }
    // Rest progress bar timer
    private fun setRestProgressBar(){
        binding.progressBar.progress = restProgress
        restTimer = object :CountDownTimer(10000,1000){
            override fun onTick(p0: Long) {
                restProgress++
                binding.progressBar.progress = 10 - restProgress
                binding.tvTimer.text= (10 - restProgress).toString()
            }
    //this is what it should happen when the timer is finished or 10 seconds have passed
            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setupExerciseView()
            }
        }.start()


    }
    // Exercise Progress Bar timer
    private fun setExerciseProgressBar(){
        binding.progressBarExercise.progress = exerciseProgress
        exerciseTimer = object :CountDownTimer(30000,1000){
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding.progressBarExercise.progress = 30 - exerciseProgress
                binding.tvExerciseTimer.text= (30 - exerciseProgress).toString()
            }
            //this is what it should happen when the timer is finished or 10 seconds have passed
            override fun onFinish() {
                if(currentExercisePosition< exerciseList?.size!!-1){
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setupRestView()
                }else{
                    finish()//So that when we press back we dont get to the last activity screen
                    //but we get back to the Home Screen/ Start screen
                    //Finishing the Exercise Activity
                    val intent = Intent(this@ExerciseActivity,FinishActivity::class.java)
                    startActivity(intent)

                }
            }
        }.start()


    }
    private fun setupRestView(){
        try {
            player = MediaPlayer.create(applicationContext,R.raw.press_start)
            player!!.isLooping = false
            player!!.start()

        }catch (e:Exception){
            e.printStackTrace()
        }

       //Starting a media player can always go wrong

        binding.llRestView.visibility = View.VISIBLE
        binding.llExerciseView.visibility = View.GONE
        if(restTimer!= null){
            restTimer!!.cancel()
            restProgress= 0
        }
        binding.tvUpcomingExerciseName.text = exerciseList!![currentExercisePosition+1].getName()
        setRestProgressBar()


    }
    private fun setupExerciseView(){
        binding.llRestView.visibility = View.GONE
        binding.llExerciseView.visibility =View.VISIBLE
        if(exerciseTimer!= null){
            exerciseTimer!!.cancel()
            exerciseProgress= 0
        }
        speakOut(exerciseList!![currentExercisePosition].getName())
        setExerciseProgressBar()
        binding.ivImage.setImageResource(exerciseList!![currentExercisePosition].getImage())
// ids not to be confused with exercise list indices
        binding.tvExerciseName.text = exerciseList!![currentExercisePosition].getName()

    }
// this is a function that we don't call manually , but the system calls automatically
    override fun onInit(status: Int) {
    if(status ==TextToSpeech.SUCCESS) {
        val result = tts!!.setLanguage(Locale.US)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            Log.e("TTS", "The language specified is not supported!")
    }else{
        Log.e("TTS","Initialization failed!")
    }

    }
    private fun speakOut(text:String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }
    private fun setUpExerciseStatusRecyclerView(){
        binding.rvExerciseStatus.layoutManager= LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!,this)
        binding.rvExerciseStatus.adapter = exerciseAdapter
    }
    private fun customDialogForBackButton(){
        val customDialog = Dialog(this)
        //val bind = DialogCustomBackConfirmationBinding.inflate(LayoutInflater.from(this))
        //customDialog.setContentView(bind.root)
        customDialog.findViewById<Button>(R.id.tvYes).setOnClickListener{
            finish()
            customDialog.dismiss()
        }
        customDialog.findViewById<Button>(R.id.tvNo).setOnClickListener{

            customDialog.dismiss()
        }
        customDialog.show()
    }
}