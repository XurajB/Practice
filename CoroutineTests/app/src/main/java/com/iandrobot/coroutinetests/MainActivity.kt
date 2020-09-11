package com.iandrobot.coroutinetests

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // there may be multiple coroutines running on a single thread
        // every coroutine has to start with a scope
        // GlobalScope means this coroutine will live as long as this application lives
        // coroutine launched from GlobalScope will be executed in a separate thread, so this will be async
        GlobalScope.launch {
            delay(1000) // similar to thread.sleep but it will only block current coroutine not the whole thread.
            // if all coroutines are put to sleep, this is equivalent to thread.sleep. Delay is a suspend function (put cursor and press ctrl+q)
            Log.d(TAG, "Coroutine says hello from thread ${Thread.currentThread().name})")
        }
        Log.d(TAG, "Coroutine says hello from thread ${Thread.currentThread().name})") // this is main thread
        // we can see two both logs were executed from different thread which proves GlobalScope being a different thread

        // NOTE: If the main thread finishes work, then all other threads will be cancelled even if the coroutines started in a different threads
        // finish main thread -> quiting the app

        // suspend function: they can only be executed inside another suspend function or from a coroutine.
        GlobalScope.launch {
            val response = doNetworkCall()
            val response2 = doNetworkCall2()
            // NOTE: since we called both suspend function from same coroutine: the total delay will be 2000L before we go to next line
            Log.d(TAG, response)
            Log.d(TAG, response2)
        }

        ///////////////////////
        // Coroutine context
        // coroutine are always started in a specific context and context will describe in which thread the coroutine will be started in
        // launch function can take a dispatcher
        // Dispatchers: MAIN: starts coroutine in the main thread (useful if we need to do UI operations from within the coroutine)
        // IO: used for data operations, networking
        // Default: complex/long running operations
        // Unconfined: not confined to specific thread, it will stay in the thread that suspend function resumed
        GlobalScope.launch(Dispatchers.IO) {
            val response = doNetworkCall2()
        }
        // we can also pass-in our own thread
        GlobalScope.launch(newSingleThreadContext("MyThread")) {
            val response = doNetworkCall()
        }

        // really useful thing about coroutine context is we can easily switch context from within a coroutine
        // we can do network call from IO context and then switch to Main to update UI
        GlobalScope.launch(Dispatchers.IO) {
            val response = doNetworkCall()
            Log.d(TAG, "starting network in ${Thread.currentThread().name}")
            // switch to the main thread
            withContext(Dispatchers.Main) {
                Log.d(TAG, "setting text in ${Thread.currentThread().name}")
                tv.text = response
            }
        }

        ////////////////////
        // runBlocking
        // this can be used in unit tests
        runBlocking { // this runs in the main thread
            delay(100L) // this will actually block the thread. similar to Thread.sleep
            // calling delay on the Dispatchers.MAIN only blocks the current coroutine not the Main thread

            // we can also start another coroutine, it will use the same coroutine scope that started it
            launch(Dispatchers.IO) {
                doNetworkCall()
            }
        }

        //////
        // coroutine jobs
        // whenever we launch a coroutine, it returns a job
        val job = GlobalScope.launch(Dispatchers.Default) {
            repeat(5) {
                Log.d(TAG, "still working on coroutine...")
                delay(1000L)
            }
        }
        // if we want to wait until it finishes, then we can just join
        runBlocking {
            job.join()
            Log.d(TAG, "continuing with the main thread...")

            // we can also cancel the job
            // in this case the job already ran since we waited until it finishes using job.join()
            // cancellation is cooperative, that means coroutine needs to be setup for cancellation
            job.cancel() // this does not mean cancelling because our coroutine might be busy performing calculation so we need to check
        }

        val job2 = GlobalScope.launch(Dispatchers.Default) {
            Log.d(TAG, "starting long running calculation..")
            for (i in 40..50) {
                if (isActive) { // this will check if the job has been cancelled
                    Log.d(TAG, "result for ${i}: ${fib(i)}")
                }
            }
            Log.d(TAG, "end of long running calculation..")
        }
        runBlocking {
            delay(3000L)
            job2.cancel()
        }

        // we can also use withTimeout suspend function to end long running calculation after some timeout
        GlobalScope.launch(Dispatchers.Default) {
            Log.d(TAG, "starting long running calculation..")
            withTimeout(5000L) { // equivalent to runBlocking and delaying 5000L and cancelling
                for (i in 40..50) {
                    if (isActive) { // this will check if the job has been cancelled
                        Log.d(TAG, "result for ${i}: ${fib(i)}")
                    }
                }
            }
            Log.d(TAG, "end of long running calculation..")
        }

        //////////////
        // Async and Await
        // if we run two suspend function in a coroutine, then they are sequential by default. the first one runs first and then second one runs after first finishes
        GlobalScope.launch(Dispatchers.IO) {
            val time = measureTimeMillis {
                val response1 = doNetworkCall()
                val response2 = doNetworkCall2()
                // network call 2 will only start after network call 1 finishes
                // all together they will take 2000L
            }
            Log.d(TAG, "total time: $time")
        }

        // however we want these two network calls happen at the same time.
        // we could start a new coroutine for each suspend function
        GlobalScope.launch(Dispatchers.IO) {
            val time = measureTimeMillis {
                var answer1: String? = null
                var answer2: String? = null

                val job1 = launch {
                    answer1 = doNetworkCall()
                }
                val job2 = launch {
                    answer2 = doNetworkCall2()
                }
                // without joining, the lines below will immediately execute and both answers will be null
                job1.join()
                job2.join()

                Log.d(TAG, "answer from 1: $answer1")
                Log.d(TAG, "answer from 2: $answer2")
            }
            // this time it will only take 1000L
            Log.d(TAG, "total time: $time")
        }

        // instead of doing all of above, we can use Async
        // Async ans Await is preferred way to do parallel work
        GlobalScope.launch(Dispatchers.IO) {
            val time = measureTimeMillis {
                // async returns a deferred type
                val answer1 = async {
                    doNetworkCall() // last line will be returned
                }
                val answer2 = async {
                    doNetworkCall2()
                }
                Log.d(TAG, "answer from 1: ${answer1.await()}") // wait until it is available
                Log.d(TAG, "answer from 2: ${answer2.await()}")
            }
            // it will only take 1000L
            Log.d(TAG, "total time: $time")
        }

        /////////////////////////
        // LifecycleScope and ViewModelScope
        //// most of the time it is bad practice to use GlobalScope because we rarely need a coroutine to live until the lifetime of an app
        // two predefined scope: LifecycleScope and ViewModelScope, we need to add dependencies in our gradle to use these
        button.setOnClickListener {
            // this will always be active
            GlobalScope.launch {
                while(true) {
                    delay(1000L)
                    Log.d(TAG, "still running...")
                }
            }
            // after 5 seconds we start new activity and finish this one
            // since we started the coroutine above in GlobalScope, it will continue to run even if we finished the first activity
            // this can cause memory leaks if that coroutine is using resources from previous activity
            GlobalScope.launch {
                delay(5000L)
                Intent(this@MainActivity, SecondActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }

        // to solve this problem, we use LifecycleScope
        // this will destroy all coroutines as soon as the lifecycle of the activity that started them is destroyed
        // however if it is doing a long running task - we might have to check if it is active, otherwise it might run until the long task is done
        button2.setOnClickListener {
            // this will work with both Activity and Fragment
            lifecycleScope.launch {
                while(isActive) { // we want to check isActive if we are running a long task, because even if the activity/fragment may call it to cancel, it may not
                    delay(1000L)
                    Log.d(TAG, "still running...")
                }
            }
            // after 5 seconds we start new activity and finish this one
            // since we started the coroutine above in GlobalScope, it will continue to run even if we finished the first activity
            // this can cause memory leaks if that coroutine is using resources from previous activity
            GlobalScope.launch {
                delay(5000L)
                Intent(this@MainActivity, SecondActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }
        // ViewModelScope is similar to LifecycleScope, it will keep coroutine alive as long as the view model is alive

        ////////////
        // Coroutines can also be used with Firebase firestore and Retrofit, we will have to import ktx version of those libraries
    }

    private suspend fun doNetworkCall(): String {
        delay(1000L)
        return "network response"
    }

    private suspend fun doNetworkCall2(): String {
        delay(1000L)
        return "network response2"
    }

    fun fib(num: Int): Long {
        if (num == 0 || num == 1) {
            return num.toLong()
        }
        return fib(num - 2) + fib(num - 1)
    }
}