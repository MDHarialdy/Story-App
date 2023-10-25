package com.mdh.storyapp

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class MailEditText: AppCompatEditText, View.OnTouchListener {
        constructor(context: Context) : super(context) {
            init()
        }

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
            init()
        }

        constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
            init()
        }

        private fun init() {
            val emailRegex = Regex("""[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""")
            setOnTouchListener(this)
            addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    // Do nothing.
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val isValidEmail = emailRegex.matches(s.toString())
                    if (!isValidEmail) {
                        setError("Email tidak valid", null)
                    } else {
                        error = null
                    }
                }
                override fun afterTextChanged(s: Editable) {
                    // Do nothing.
                }
            })
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            return false
        }
}