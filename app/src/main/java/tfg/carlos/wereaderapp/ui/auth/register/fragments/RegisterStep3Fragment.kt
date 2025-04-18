package tfg.carlos.wereaderapp.ui.auth.register.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.databinding.FragmentRegisterStep3Binding
import tfg.carlos.wereaderapp.ui.auth.register.AvatarAdapter
import tfg.carlos.wereaderapp.ui.auth.register.RegisterActivity

class RegisterStep3Fragment : Fragment() {
    private var _binding: FragmentRegisterStep3Binding? = null
    private val binding get() = _binding!!

    private val authors = listOf(
        "J.K. Rowling", "J.R.R. Tolkien", "Isabel Allende",
        "Stephen King", "George R.R. Martin", "Brandon Sanderson",
        "Agatha Christie", "Gabriel García Márquez", "C.S. Lewis",
        "Jane Austen", "Mark Twain", "Ernest Hemingway",
        "F. Scott Fitzgerald", "Harper Lee", "Virginia Woolf",
        "Ray Bradbury", "H.G. Wells", "Arthur C. Clarke",
        "Paul Pen", "Carlos Ruiz Zafón", "Mario Vargas Llosa",
        "Julio Cortázar", "Gabriel García Márquez", "Javier Marías",
        "Mario Benedetti", "Pablo Neruda", "Jorge Luis Borges",
        "Miguel de Cervantes", "Lope de Vega", "Antonio Machado",
        "Federico García Lorca", "Rafael Alberti", "Luis Cernuda",
        "Alfonsina Storni", "Gloria Fuertes", "Ana Rossetti",
        "Michael Ende", "Philip Pullman", "Neil Gaiman",
        "Terry Pratchett", "Douglas Adams", "Isaac Asimov",
        "Arthur Conan Doyle", "Raymond Chandler", "Arturo Pérez-Reverte",
        "Jules Verne", "Herman Melville", "Jack London",
    )

    private val genres = listOf(
        "Fantasía", "Ciencia Ficción", "Romántica",
        "Thriller", "Misterio", "Histórica", "Terror", "Biografía", "No Ficción", "Autoayuda",
        "Desarrollo Personal", "Ciencia", "Tecnología", "Filosofía", "Psicología", "Sociología",
        "Economía", "Política", "Arte", "Literatura", "Cocina", "Viajes", "Deportes",
        "Salud", "Bienestar", "Educación", "Infantil", "Juvenil", "Clásicos", "Contemporánea",
        "Poesía", "Ensayo", "Teatro", "Cuentos", "Relatos Cortos", "Novela Gráfica",
        "Manga", "Cómic", "Literatura Infantil", "Literatura Juvenil", "Literatura Erótica",
        "Western", "Romántica Histórica", "Romántica Contemporánea", "Romántica Paranormal",
        "Literatura Fantástica", "Literatura Histórica", "Literatura de Viajes", "Literatura de Aventura",
        "Literatura de Ciencia Ficción", "Literatura de Terror", "Literatura de Misterio",
        "Literatura de Suspense", "Literatura de Fantasía Épica", "Literatura de Fantasía Urbana",
        "Histórica española",

    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterStep3Binding.inflate(inflater, container, false)

        val authorAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_dropdown_item_1line, authors)
        binding.authorDropdown.setAdapter(authorAdapter)

        val genreAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_dropdown_item_1line, genres)
        binding.genreDropdown.setAdapter(genreAdapter)

        binding.authorDropdown.threshold = 1
        binding.genreDropdown.threshold = 1

        val avatarList = listOf(
            R.drawable.avatar1,
            R.drawable.avatar2,
            R.drawable.avatar3,
            R.drawable.avatar4,
            R.drawable.avatar5,
            R.drawable.avatar6,
            R.drawable.avatar7,
            R.drawable.avatar8,
        )

        var selectedAvatar = R.drawable.avatar1 // valor por defecto

        val adapter = AvatarAdapter(avatarList) { avatarResId ->
            selectedAvatar = avatarResId
        }

        binding.avatarRecyclerView.adapter = adapter

        binding.btnRegister.setOnClickListener {
            val author = binding.authorDropdown.text.toString().trim()
            val genre = binding.genreDropdown.text.toString().trim()

            var isValid = true

            // Limpiar errores previos
            binding.authorDropdownLayout.error = null
            binding.genreDropdownLayout.error = null

            // Validaciones
            if (author.isEmpty()) {
                binding.authorDropdownLayout.error = getString(
                    tfg.carlos.wereaderapp.R.string.register_step3_error_empty_author)
                isValid = false
            }

            if (genre.isEmpty()) {
                binding.genreDropdownLayout.error = getString(
                    tfg.carlos.wereaderapp.R.string.register_step3_error_empty_genre)
                isValid = false
            }

            // Validar que el autor y el género están en las listas
            if (!authors.contains(author)) {
                binding.authorDropdownLayout.error = getString(
                    tfg.carlos.wereaderapp.R.string.register_step3_error_invalid_author)
                isValid = false
            }

            if (!genres.contains(genre)) {
                binding.genreDropdownLayout.error = getString(
                    tfg.carlos.wereaderapp.R.string.register_step3_error_invalid_genre)
                isValid = false
            }

            if (isValid) {
                val activity = requireActivity() as RegisterActivity
                val data = activity.getRegisterData()

                data.authorFav = author
                data.genderFav = genre
                data.avatar = selectedAvatar

                activity.registerUser()
            }

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
