package iooojik.casein.ui.companyinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import iooojik.casein.R


class InfoPart(private var position: Int) : Fragment(position) {

    private lateinit var rootView : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_info_part, container, false)
        initialize()
        return rootView
    }

    private fun initialize(){

    }

}