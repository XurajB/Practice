package com.iandrobot.androidtestpractice

import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
// we need this test our fragments, currently creating a test activity is the only way
// add this to debug manifest
@AndroidEntryPoint
class HiltTestActivity : AppCompatActivity() {
}