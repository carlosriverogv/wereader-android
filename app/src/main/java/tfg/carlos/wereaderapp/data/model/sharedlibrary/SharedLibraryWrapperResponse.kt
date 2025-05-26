package tfg.carlos.wereaderapp.data.model.sharedlibrary


import com.google.gson.annotations.SerializedName

data class SharedLibraryWrapperResponse(
    @SerializedName("sharedLibrary")
    val sharedLibrary: SharedLibrary?
)