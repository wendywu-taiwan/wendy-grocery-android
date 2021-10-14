package wendy.grocery.android.utilities.navigation

import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator


/**
 * Use the command pattern to communicate between ViewModel and Fragment.
 * These are the commands that we use for navigation
 *
 * See FragmentExtensions.kt for the usage
 *
 */
sealed class NavigationCommand {
    data class To(val directions: NavDirections, val options: NavOptions? = null, var extras : Navigator.Extras? = null): NavigationCommand()
    object Back: NavigationCommand()
    data class BackTo(val destinationId: Int, val inclusive: Boolean): NavigationCommand()
}