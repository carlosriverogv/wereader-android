package tfg.carlos.wereaderapp.ui.auth.register.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.databinding.FragmentRegisterStep2Binding
import tfg.carlos.wereaderapp.ui.auth.register.RegisterActivity

class RegisterStep2Fragment : Fragment() {
    private var _binding: FragmentRegisterStep2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterStep2Binding.inflate(inflater, container, false)

        binding.btnNext1.setOnClickListener {
            val email = binding.inputEmail.text.toString().trim()
            val password = binding.inputPassword.text.toString().trim()
            val repeatPassword = binding.inputRepeatPassword.text.toString().trim()

            var isValid = true

            // Reset errores
            binding.emailLayout.error = null
            binding.passwordLayout.error = null
            binding.passwordRepeatLayout.error = null

            // Validar email
            if (email.isEmpty()) {
                binding.emailLayout.error = getString(R.string.register_step2_error_empty_email)
                isValid = false
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailLayout.error = getString(R.string.register_step2_error_invalid_email)
                isValid = false
            }

            // Validar contraseñas
            if (!password.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$"))) {
                binding.passwordLayout.error =
                    getString(R.string.register_step2_error_invalid_password)
                isValid = false
            }
            if (password.isEmpty()) {
                binding.passwordLayout.error = getString(R.string.register_step2_error_empty_password)
                isValid = false
            }

            if (repeatPassword.isEmpty()) {
                binding.passwordRepeatLayout.error = getString(R.string.register_step2_error_empty_repeat_password)
                isValid = false
            }

            if (password != repeatPassword) {
                binding.passwordRepeatLayout.error = "Las contraseñas no coinciden"
                isValid = false
            }

            // Si los campos son correctos
            if (isValid) {
                val activity = requireActivity() as RegisterActivity
                val data = activity.getRegisterData()
                data.email = email
                data.password = password

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
