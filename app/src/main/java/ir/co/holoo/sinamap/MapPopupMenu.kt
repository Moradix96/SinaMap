package ir.co.holoo.sinamap

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import ir.co.holoo.sinamap.databinding.MainPopupMenuBinding

abstract class MapPopupMenu(private val context: Context) {
    private lateinit var binding: MainPopupMenuBinding
    private lateinit var popupWindow: PopupWindow

    abstract fun onRouteClick()
    abstract fun onAddClick()

    fun showPopup(view: View) {
        val inflater = LayoutInflater.from(context)
        binding = MainPopupMenuBinding.inflate(inflater)

        // Create the popup window
        popupWindow = PopupWindow(
            binding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.elevation = 10f

        // Set a click listener for the menu items
        binding.menuRoute.setOnClickListener {
            onRouteClick()
//            val dialogFragment = CcGroupInfoFragment()
//            dialogFragment.show((context as FragmentActivity).supportFragmentManager, "CcGroupInfoFragment")
            dismissPopup()
        }

        binding.menuAdd.setOnClickListener {
            // Handle Leave Group menu item click
            //val dialogFragment = CcAcceptableAlertDialogFragment.newInstance(false,"Are You sure You want to leave the Group ‘NHS Group’?","You have left the Group ‘NHS Group’",1)
            //dialogFragment.show((context as FragmentActivity).supportFragmentManager, "CcAcceptableAlertDialogFragment")

            onAddClick()
            dismissPopup()

        }

        // Set background drawable for elevated shadow effect
        val cardView = binding.root
        cardView.setBackgroundResource(R.drawable.popup_menu_background)

        // Show the popup window
        popupWindow.showAsDropDown(view, 0, 0)
    }

    private fun dismissPopup() {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }
}