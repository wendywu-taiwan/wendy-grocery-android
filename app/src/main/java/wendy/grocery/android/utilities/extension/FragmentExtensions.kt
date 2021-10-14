package wendy.grocery.android.utilities.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import wendy.grocery.android.utilities.navigation.NavigationCommand

/**
 * Attach observer to navigation live data
 *
 * @param navEvent : received navigation event
 * @param rootId : root navigation id
 */
fun Fragment.observeNavigationEvent(navEvent: LiveData<NavigationCommand>, rootId: Int){
    navEvent.observe(viewLifecycleOwner, Observer { command ->
        navigate(command, rootId)
    })
}

/**
 * Handle generic navigation
 *
 * @param command
 * @param rootId
 */
fun Fragment.navigate(command: NavigationCommand, rootId: Int) {
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
