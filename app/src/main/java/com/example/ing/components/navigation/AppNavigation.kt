package com.example.ing.components.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ing.screens.HomeScreen
import com.example.ing.screens.JobsScreen
import com.example.ing.screens.ToolsScreen
import com.example.ing.screens.forms.NewJobScreen
import com.example.ing.screens.forms.NewToolScreen
import com.example.ing.screens.ToolStatusUpdateScreen
import com.example.ing.screens.forms.EditToolScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Jobs : Screen("jobs")
    object Tools : Screen("tools")
    //object Connection : Screen("connection")
    object NewJob : Screen("jobs/new_job")
    object NewTool : Screen("tools/new_tool")

    data class EditTool(val toolId: String) : Screen("tools/edit/{$TOOL_ID_ARG}") {
        companion object {
            const val TOOL_ID_ARG = "toolId"
            fun routeForId(id: String) = "tools/edit/$id"
        }
    }

    data class JobDetail(val jobId: String) : Screen("jobs/detail/{$JOB_ID_ARG}") {
        companion object {
            const val JOB_ID_ARG = "jobId"
            fun routeForId(id: String) = "jobs/detail/$id"
        }
    }
    data class ToolStatusUpdate(val jobId: String, val newStatus: String) : Screen("tools/status/{$JOB_ID_ARG}/{$NEW_STATUS_ARG}") {
        companion object {
            const val JOB_ID_ARG = "jobId"
            const val NEW_STATUS_ARG = "newStatus"
            // --- ASEGÚRATE DE QUE ESTA LÍNEA USE UN SIGNO DE IGUAL ---
            fun routeForIdAndStatus(id: String, status: String) = "tools/status/$id/$status"
        }
    }

}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Jobs.route) {
            JobsScreen(navController = navController)
        }
        composable(Screen.Tools.route) {
            ToolsScreen(navController = navController)
        }
        /*composable(Screen.Connection.route) {
            ConnectionScreen()
        }*/
        composable(Screen.NewJob.route) {
            NewJobScreen(navController = navController)
        }
        composable(Screen.NewTool.route) {
            NewToolScreen(navController = navController)
        }

        composable(
            route = Screen.EditTool("").route, // Usamos una instancia vacía para obtener la plantilla de la ruta
            arguments = listOf(
                navArgument(Screen.EditTool.TOOL_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val toolId = backStackEntry.arguments?.getString(Screen.EditTool.TOOL_ID_ARG)
            if (toolId != null) {
                EditToolScreen(navController = navController, toolId = toolId)
            }
        }

        composable(
            route = Screen.JobDetail("{jobId}").route,
            arguments = listOf(
                androidx.navigation.navArgument(Screen.JobDetail.JOB_ID_ARG) {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString(Screen.JobDetail.JOB_ID_ARG) ?: ""
            com.example.ing.screens.JobDetailScreen(navController = navController, jobId = jobId)
        }
        composable(
            route = Screen.ToolStatusUpdate("", "").route, // Ruta base con la nueva estructura
            arguments = listOf(
                navArgument(Screen.ToolStatusUpdate.JOB_ID_ARG) { type = NavType.StringType },
                navArgument(Screen.ToolStatusUpdate.NEW_STATUS_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Extrae ambos argumentos
            val jobId = backStackEntry.arguments?.getString(Screen.ToolStatusUpdate.JOB_ID_ARG) ?: ""
            val newStatus = backStackEntry.arguments?.getString(Screen.ToolStatusUpdate.NEW_STATUS_ARG) ?: ""

            // Llama a la pantalla con ambos argumentos
            ToolStatusUpdateScreen(
                navController = navController,
                jobId = jobId,
                newStatus = newStatus
            )
        }
    }
} 