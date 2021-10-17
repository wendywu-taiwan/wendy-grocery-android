package wendy.grocery.android.utilities.extension

import android.content.res.Configuration
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import wendy.grocery.android.utilities.livedata.Event
import wendy.grocery.android.utilities.livedata.EventObserver
import wendy.grocery.android.utilities.navigation.NavigationCommand
import java.util.*

/**
 * Attach observer to navigation live data
 *
 * @param navEvent : received navigation event
 */
fun Fragment.observeNavigationEvent(navEvent: LiveData<Event<NavigationCommand>>){
    navEvent.observe(viewLifecycleOwner, EventObserver { command ->
        navigate(command)
    })
}

/**
 * Handle generic navigation
 *
 * @param command
 * @param rootId
 */
fun Fragment.navigate(command: NavigationCommand) {
    when (command) {
        is NavigationCommand.To -> {
            // if action id can't find in currentDestination means it's not the correct directions
            this.findNavController().currentDestination?.getAction(command.directions.actionId) ?: return

            command.extras?.let {
                findNavController().navigate(command.directions, it)
            } ?: kotlin.run {
                findNavController().navigate(command.directions, command.options)
            }
        }

        is NavigationCommand.Back -> {
            findNavController().navigateUp()
        }

        is NavigationCommand.BackTo -> {
            findNavController().popBackStack(command.destinationId, command.inclusive)
        }
    }
}

/** Get string from locale file with given resource id */
fun Fragment.getLocaleStringResource(
    resourceId: Int,
    requestedLocale: Locale = Locale.ENGLISH,
): String {
    val result: String
    val config = Configuration(resources.configuration)
    config.setLocale(requestedLocale)
    result = this.requireContext().createConfigurationContext(config).getText(resourceId).toString()

    return result
}
