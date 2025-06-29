package com.example.googlecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.googlecalculator.ui.theme.GoogleCalculatorTheme
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

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
    Column(modifier = modifier
        .fillMaxWidth()
        .background(Color(0xffebeffa))
        .verticalScroll(rememberScrollState())
    ) {
        var history by remember {
            mutableStateOf(listOf<String>())
        }
        var exp by remember { mutableStateOf("") }
        var result by remember {
            mutableStateOf("")
        }
        History(history)
        NavBar()
        Expression(exp, { newExp ->
            exp = newExp
            result = calculate(exp)
        })
        Result(result)
        CalculatorBtns { button ->
            if (button == "=" && result != "error") {
                history = (listOf("$exp = $result") + history).toMutableList() // Add to history
            }
            exp = onButtonClick(exp, button)
            result = calculate(exp)
        }
    }
}

private fun calculate(exp:String) : String{
    if(exp.isEmpty()) return ""
    if (exp.last() in listOf('+', '-', 'x', '÷', '%')) {
        return ""
    }
    return try{
        val context = Context.enter()
        context.optimizationLevel = -1
        val scope: Scriptable = context.initStandardObjects()
        val processedExp = exp.replace("x","*").replace("÷", "/").replace("%","/100")
        if(Regex("/0+($|[^0-9])").containsMatchIn(processedExp)){
            return "Can't divide by 0"
        }
        if(Regex("√-\\d+").containsMatchIn(processedExp)){
            return "Keep it real"
        }
        val result = context.evaluateString(
            scope,processedExp,"Rhino",1,null
        )
        Context.exit()
        val resultString = result.toString()
        if (resultString.endsWith(".0")) resultString.dropLast(2) else resultString
    }catch (
        e : Exception
    ){
        "Format error"
    }
}
private fun onButtonClick(currExp:String, button: String):String{
    return when(button){
        "AC"->""
        "⌫"-> if(currExp.isNotEmpty()) currExp.dropLast(1) else currExp
        "="-> currExp
        "( )"-> {
            val openCount = currExp.count{ it == '(' }
            val closeCount = currExp.count{ it == ')' }
            if(openCount>closeCount && currExp.isNotEmpty() && currExp.last().isDigit()){
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
            DropdownMenuItem(text = {Text("History")}, onClick = { /*TODO*/ }) //the history composable
            DropdownMenuItem(text = {Text("Choose theme")}, onClick = { /*TODO*/ }) //dark & light mode
            DropdownMenuItem(text = {Text("Privacy Policy")}, onClick = { /*TODO*/ }) // privacy policy doc/web
            DropdownMenuItem(text = {Text("Send Feedback")}, onClick = { /*TODO*/ }) //cantry adding a form here
            DropdownMenuItem(text = {Text("Help")}, onClick = { /*TODO*/ }) // make an add query box? or just add faqs
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
    val isError = result in listOf("Can't divide by 0","Keep it real","error")
    Text(
        text = result,
        fontSize = 36.sp,
        color = if (isError) Color.Red else Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun History(history: List<String>,modifier: Modifier = Modifier){

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
                    .size(64.dp),
                shape = CircleShape
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