package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.dataStore.AppDataStore
import futur.apps.composeproject1.quiz.ui.theme.AllPalettes
import futur.apps.composeproject1.quiz.ui.theme.AppPalette
import futur.apps.composeproject1.quiz.ui.theme.BluePalette
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val dataStore: AppDataStore
) : ViewModel() {

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> get() = _isLoaded

    // both flows
    private val darkFlow = dataStore.isDarkThemeEnabled
    private val paletteFlow = dataStore.selectedPaletteName

    val isDarkTheme: StateFlow<Boolean> = darkFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _selectedPaletteName: StateFlow<String> = paletteFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, BluePalette.name)
    val selectedPaletteName: StateFlow<String> get() = _selectedPaletteName

    init {
        // Wait for both prefs to load at least once before marking loaded
        viewModelScope.launch {
            combine(darkFlow, paletteFlow) { _, _ -> }
                .first() // suspend until first pair emitted
            _isLoaded.value = true
        }
    }

    fun toggleTheme(enabled: Boolean) {
        viewModelScope.launch { dataStore.saveDarkThemePreference(enabled) }
    }

    fun setPalette(name: String) {
        viewModelScope.launch { dataStore.setSelectedPalette(name) }
    }

    fun getPalette(): AppPalette =
        AllPalettes.find { it.name == _selectedPaletteName.value } ?: BluePalette
}
