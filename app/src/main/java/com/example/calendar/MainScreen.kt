package com.example.calendar

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.APRIL
import java.util.Calendar.AUGUST
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.DAY_OF_WEEK
import java.util.Calendar.DECEMBER
import java.util.Calendar.JANUARY
import java.util.Calendar.JULY
import java.util.Calendar.JUNE
import java.util.Calendar.MARCH
import java.util.Calendar.MAY
import java.util.Calendar.MONTH
import java.util.Calendar.NOVEMBER
import java.util.Calendar.OCTOBER
import java.util.Calendar.SEPTEMBER
import java.util.Calendar.SUNDAY
import java.util.Calendar.YEAR

// Функция, отображающая календарь
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat")
@Composable
fun MyCalendar(
    modifier: Modifier = Modifier
) {
    val clockTime by rememberClockState() // Получаем текущее время
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        // Предварительные расчеты
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = clockTime // инициализируем календарь текущим временем
        val dayOfMonth = calendar.get(DAY_OF_MONTH) // текущий день месяца
        calendar.set(DAY_OF_MONTH, 1) // перевыставляем календарь на 1-е число
        val firstUS = calendar.get(DAY_OF_WEEK) // день недели 1-го числа
        val first = if (firstUS == SUNDAY) 7 else firstUS - 1 // коррекция в российский формат
        val month = calendar.get(MONTH) // текущий месяц
        calendar.set(MONTH, month-1)
        val monthPrev = calendar.get(MONTH) // предыдущий месяц
        val year = calendar.get(YEAR) // текущий год
        val daysInPrevMonth = daysInMonth (monthPrev, year) // число дней в предыдущем месяце (можно с текущим годом, так как номер года важен только для февраля)
        val daysInCurrMonth = daysInMonth (month, year) // число дней в текущем месяце
        // Вывод заголовка
        Row {
            val nameOfDay = arrayOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
            for (col: Int in 0..<nameOfDay.size) {
                Column {
                    Box(contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(48.dp)
                            .height(32.dp)
                            //.background(color = Color.LightGray)
                            .padding(horizontal = 8.dp, vertical = 0.dp)) {
                        Text(text = nameOfDay[col],
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 0.dp)
                        )
                    }
                }

            }
        }
        // Вывод календаря в месячном формате
        for (row: Int in 0..5) {
            Row {
                for (col: Int in 2..8) {
                    val dayCounter = row * 7 + col - first // соответствие номера дня столбцу с учетом дня недели
                    val dayCounterFixed = if (dayCounter <= 0) {
                        dayCounter + daysInPrevMonth // Предыдущий месяц
                    } else {
                        if (dayCounter > daysInCurrMonth) {
                            dayCounter - daysInCurrMonth // Следующий месяц
                        }
                        else dayCounter // Текущий месяц
                    }
                    val backColor = if (dayCounter == dayOfMonth) {
                        Color.Cyan // Текущая дата
                    } else {
                        if (dayCounter == dayCounterFixed) {
                            Color.White // Текущий месяц
                        }
                        else {
                            Color.LightGray // Соседние месяцы
                        }
                    }
                    Column {
                        Box(contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .width(48.dp)
                                .height(48.dp)
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .border(width = 1.dp, color = Color.Gray)
                                .background(color = backColor)) {
                            Text(text = "$dayCounterFixed",
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                }
            }
        }
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        Text(
            text = "Дата: ${dateFormatter.format(clockTime)}",
            fontSize = 32.sp,
        )
        val timeFormatter = SimpleDateFormat("HH:mm:ss")
        Text(
            text = "Время: ${timeFormatter.format(clockTime)}",
            fontSize = 32.sp,
        )
    }
}

// Функция, реализующая таймер и возвращающая текущее время
@Composable
fun rememberClockState() : MutableState<Long> {
    val step: Long = 500
    val clockTime = remember { mutableLongStateOf(currentTimeMillis()) }
    LaunchedEffect(clockTime, step) {
        while (true) {
            clockTime.longValue = currentTimeMillis()
            delay(step)
        }
    }
    return clockTime
}

// Вычисление числа дней в месяце
fun daysInMonth(month: Int, year: Int) : Int {
    return when (month) {
        JANUARY, MARCH, MAY, JULY, AUGUST,  OCTOBER, DECEMBER -> 31
        APRIL, JUNE, SEPTEMBER, NOVEMBER, -> 30
        else -> if ((year % 4 == 0 && year % 100 != 0) or (year % 400 == 0)) 29 else 28
    }
}