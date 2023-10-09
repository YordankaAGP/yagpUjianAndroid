package com.bcaf.yagp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bcaf.yagp.fragment.Collection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentRoot, Collection.newInstance("",""))
                .commit()
        }
    }
}