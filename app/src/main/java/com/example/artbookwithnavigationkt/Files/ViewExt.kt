package com.example.artbookwithnavigationkt

import android.view.View
import android.view.animation.Animation
import androidx.navigation.Navigation

fun View.startAnimation(animation: Animation, onEnd: () -> Unit){
    animation.setAnimationListener(object : Animation.AnimationListener{
        override fun onAnimationStart(p0: Animation?) = Unit


        override fun onAnimationEnd(p0: Animation?) {
          onEnd()
        }

        override fun onAnimationRepeat(p0: Animation?) = Unit


    })
        this.startAnimation(animation)

}