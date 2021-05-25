package pt.rfernandes.loopuiux.ui.activities

import android.content.Intent
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import pt.rfernandes.loopuiux.R
import pt.rfernandes.loopuiux.databinding.ActivitySplashScreenBinding
import pt.rfernandes.loopuiux.databinding.FragmentHomeBinding

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    private lateinit var avd: AnimatedVectorDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_splash_screen)

//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        val drawable = binding.imageViewSplashScreen.drawable

        if (drawable is AnimatedVectorDrawable) {
            avd = drawable
            avd.start()
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    launchMainActivity()

                }, 3000
            )
        }

    }

    private fun launchMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}