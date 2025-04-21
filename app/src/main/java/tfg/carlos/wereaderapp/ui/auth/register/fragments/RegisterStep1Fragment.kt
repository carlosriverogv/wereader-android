package tfg.carlos.wereaderapp.ui.auth.register.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.databinding.FragmentRegisterStep1Binding
import tfg.carlos.wereaderapp.ui.auth.register.RegisterActivity

class RegisterStep1Fragment : Fragment() {
    private var _binding: FragmentRegisterStep1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterStep1Binding.inflate(inflater, container, false)

        binding.btnNext1.setOnClickListener {
            val name = binding.inputName.text.toString().trim()
            val lastname = binding.inputLastname.text.toString().trim()
            val rawTag = binding.inputTagName.text.toString().trim()
            val tag = if (rawTag.startsWith("@")) rawTag else "@$rawTag"

            var isValid = true

            // Limpiar errores previos
            binding.nameLayout.error = null
            binding.lastnameLayout.error = null
            binding.tagNameLayout.error = null

            // Validaciones
            if (name.isEmpty()) {
                binding.nameLayout.error = getString(R.string.register_step1_error_empty_name)
                isValid = false
            }

            if (lastname.isEmpty()) {
                binding.lastnameLayout.error = getString(R.string.register_step1_error_empty_lastname)
                isValid = false
            }

            if (rawTag.isEmpty()) {
                binding.tagNameLayout.error = getString(R.string.register_step1_error_empty_user_name)
                isValid = false
            } else if (!rawTag.matches(Regex("^[a-zA-Z0-9_]{3,15}$"))) {
                binding.tagNameLayout.error = getString(R.string.register_step1_error_invalid_user_name)
                isValid = false
            }

            if (isValid) {
                val activity = requireActivity() as RegisterActivity
                val data = activity.getRegisterData()

                data.name = name
                data.lastname = lastname
                data.tag = tag

                activity.goToNextStep()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
