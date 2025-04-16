package tfg.carlos.wereaderapp.ui.auth.register.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import tfg.carlos.wereaderapp.databinding.FragmentRegisterStep3Binding
import tfg.carlos.wereaderapp.ui.auth.register.RegisterActivity

class RegisterStep3Fragment : Fragment() {
    private var _binding: FragmentRegisterStep3Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterStep3Binding.inflate(inflater, container, false)

        binding.btnNext1.setOnClickListener {


            val activity = requireActivity() as RegisterActivity
            activity.goToNextStep()
        }

        return binding.root
    }
}
