package com.example.android.architecture.blueprints.todoapp.tasks

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeAndroidDefaultTaskRepository
import com.example.android.architecture.blueprints.todoapp.data.source.IDefaultTasksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito


@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
internal class TasksFragmentTest {

    private lateinit var repository: IDefaultTasksRepository

    @Before
    fun initRepository() {
        repository = FakeAndroidDefaultTaskRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanUpDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun clickTask_navigateToDetailFragmentOne() = runBlockingTest {

        // Assign
        repository.saveTask(Task("TITLE1", "DESCRIPTION1", false, "ID1"))
        repository.saveTask(Task("TITLE2", "DESCRIPTION2", false, "ID2"))
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // Act
        onView(withId(R.id.tasks_list)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText("TITLE1")), click()
            )
        )

        // Assert
        Mockito.verify(navController)
            .navigate(TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment("ID1"))
    }

    @Test
    fun clickAddTaskButton_navigateToAddEditFragment() {

        // Assign
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // Act
        onView(withId(R.id.add_task_fab)).perform(click())

        // Assert
        val expectedArguments = TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(taskId = null,
            title = ApplicationProvider.getApplicationContext<Context>()
                .getString(R.string.add_task)
        )
        Mockito.verify(navController).navigate(expectedArguments)
    }
}