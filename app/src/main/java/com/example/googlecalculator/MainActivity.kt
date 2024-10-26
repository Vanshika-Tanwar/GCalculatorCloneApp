package com.example.googlecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.googlecalculator.ui.theme.GoogleCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoogleCalculatorTheme {
                CalculatorApp()
            }
        }
    }
}

@Composable
fun Calculator(modifier: Modifier = Modifier) {
    Column(modifier = modifier //also make this column scrollable and show history
        .fillMaxWidth()
        .background(Color(0xffebeffa))
    ) {

        var exp by remember { mutableStateOf("") }
        var result by remember {
            mutableStateOf("")
        }
        History() //the history section which could be a list of all operations performed
        NavBar()
        Expression(exp, { newExp ->
            exp = newExp
            result = calculate(exp)
        })
        Result(result)
        CalculatorBtns { button ->
            exp = onButtonClick(exp, button)
            result = calculate(exp)
        }
    }
}

private fun calculate(exp:String) : String{
    if(exp.isEmpty()) return ""
    else return exp
}

private fun onButtonClick(currExp:String, button: String):String{
    return when(button){
        "AC"->""
        "⌫"-> if(currExp.isNotEmpty()) currExp.dropLast(1) else currExp
        "="-> currExp
        "( )"-> {
            val openCount = currExp.count{ it == '(' }
            val closeCount = currExp.count{ it == ')' }
            if(openCount>closeCount){
                currExp + ")"
            } else{
                currExp + "("
            }
        }
        else -> currExp+button
    }
}

@Composable
fun NavBar(modifier: Modifier = Modifier){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var expanded by remember { mutableStateOf(false) }
        Text(
            text = "Current expression",
            modifier=Modifier.weight(1f)
            )
        IconButton(onClick = { expanded = true }) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(text = {Text("History")}, onClick = { /*TODO*/ })
            DropdownMenuItem(text = {Text("Choose theme")}, onClick = { /*TODO*/ })
            DropdownMenuItem(text = {Text("Privacy Policy")}, onClick = { /*TODO*/ })
            DropdownMenuItem(text = {Text("Send Feedback")}, onClick = { /*TODO*/ })
            DropdownMenuItem(text = {Text("Help")}, onClick = { /*TODO*/ })
        }
    }
}

@Composable
fun Expression(exp: String, onExpChange:(String) -> Unit, modifier: Modifier = Modifier){
    TextField(
        value = exp,
        onValueChange = onExpChange,
        singleLine = true,
        textStyle = TextStyle(fontSize = 32.sp),
        colors = TextFieldDefaults.colors(
            cursorColor = Color.Blue,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun Result(result:String, modifier: Modifier = Modifier){
    Text(
        text = result,
        fontSize = 24.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun History(modifier: Modifier = Modifier){

}

@Composable
fun CalculatorBtns(modifier: Modifier = Modifier.background(Color.White), onButtonClick: (String)->Unit){
    val buttons = listOf(
        "AC","( )","%","÷",
        "7","8","9","x",
        "4","5","6","-",
        "1","2","3","+",
        "0",".","⌫","="
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
        ) {
        items(buttons){ button ->
            FilledTonalButton(
                onClick = { onButtonClick(button) },
                modifier= Modifier
                    .padding(5.dp)
                    .size(64.dp)
                    .clip(CircleShape)
            ) {
                Text(
                    text = button,
                    fontSize = 28.sp
                )
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorApp() {
    Calculator()
}