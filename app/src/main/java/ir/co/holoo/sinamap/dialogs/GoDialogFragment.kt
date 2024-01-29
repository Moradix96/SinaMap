package ir.co.holoo.sinamap.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ir.co.holoo.sinamap.R
import ir.co.holoo.sinamap.databinding.FragmentGoDialogBinding

class GoDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentGoDialogBinding
    var goListener: GoListener? = null
        set(value) {
            field = value
            println("Interface has been set.")
        }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGoDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGo.setOnClickListener {
            goListener?.go(
                binding.etLat.text.toString().toDouble(),
                binding.etLon.text.toString().toDouble()
            )
            dismiss()
        }

    }

    //abstract fun go(lat: Double, lon: Double)

    override fun getTheme(): Int {
        return R.style.CustomDialogThemeJ
    }
}

interface GoListener {
    fun go(lat: Double, lon: Double)
}