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

        /*binding.title.text = title!!

        binding.yesBtn.setOnClickListener {

            if (whatTypeIs) {
                val dialogFragment = CcGreenAcceptableAlertDialogFragment.newInstance(
                    true,
                    nextTitle!!
                )
                dialogFragment.show(parentFragmentManager, "CcAlertDialogFragment")
            } else {
                val dialogFragment = CcAlertDialogFragment.newInstance(
                    nextTitle!!,null,
                    0
                )
                dialogFragment.show(parentFragmentManager, "CcAlertDialogFragment")
            }
            dismiss()
        }*/

    }

    override fun getTheme(): Int {
        return R.style.CustomDialogThemeJ
    }
}