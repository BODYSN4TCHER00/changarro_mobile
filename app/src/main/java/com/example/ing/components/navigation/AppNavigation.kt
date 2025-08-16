package com.example.ing.components.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ing.screens.HomeScreen
import com.example.ing.screens.JobsScreen
import com.example.ing.screens.ToolsScreen
import com.example.ing.screens.forms.NewJobScreen
import com.example.ing.screens.forms.NewToolScreen
import com.example.ing.screens.ToolStatusUpdateScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Jobs : Screen("jobs")
    object Tools : Screen("tools")
    //object Connection : Screen("connection")
    object NewJob : Screen("jobs/new_job")
    object NewTool : Screen("tools/new_tool")
    data class JobDetail(val jobId: String) : Screen("jobs/detail/{$JOB_ID_ARG}") {
        companion object {
            const val JOB_ID_ARG = "jobId"
            fun routeForId(id: String) = "jobs/detail/$id"
        }
    }
    data class ToolStatusUpdate(val jobTitle: String) : Screen("tools/status/{$JOB_TITLE_ARG}") {
        companion object {
            const val JOB_TITLE_ARG = "jobTitle"
            fun routeForTitle(title: String) = "tools/status/$title"
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
            route = Screen.ToolStatusUpdate("{jobTitle}").route,
            arguments = listOf(
                androidx.navigation.navArgument(Screen.ToolStatusUpdate.JOB_TITLE_ARG) {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val jobTitle = backStackEntry.arguments?.getString(Screen.ToolStatusUpdate.JOB_TITLE_ARG) ?: ""
            ToolStatusUpdateScreen(navController = navController, jobTitle = jobTitle)
        }
    }
} 