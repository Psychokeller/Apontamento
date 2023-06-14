package com.beholder.apontamento_amspec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.beholder.apontamento_amspec.ui.theme.Apontamento_AmspecTheme
import kotlin.math.absoluteValue
import androidx.compose.foundation.lazy.LazyColumn

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Apontamento_AmspecTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Apontador()
                }
            }
        }
    }
}


@Composable
fun Apontador() {

    // Style
    val mainRowStyle = Modifier
        .padding(start = 7.dp, top = 10.dp)
        .fillMaxWidth(.9f)
    
    // Valores mutaveis
    val adicionadas = remember { mutableStateOf<Int>(0)}
    val sobras = remember { mutableStateOf<Int>(0)}
    val avarias = remember { mutableStateOf<Int>(0)}
    val totalSacas = remember { mutableStateOf<Int>(0)}

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth(.7f)
            .padding(top = 20.dp)
    ) {
        // Total desejado
        item {
            Row( mainRowStyle ){
                InputValues(valor = totalSacas, labelText = "Total de sacas")
            }
            // Sacas adicionadas
            Row( mainRowStyle ){
                InputValues(valor = adicionadas, labelText = "Sacas adicionadas/ja presentes")
            }
            // Sobras
            Row( mainRowStyle ){
                InputValues(valor = sobras, labelText = "Sobras")
            }
            // Avarias
            Row( mainRowStyle ){
                InputValues(valor = avarias, labelText = "Avarias")
            }
            Row(
            ) {
                PossibleValues(valorDesejado = totalSacas.value, sobras = sobras.value + avarias.value, adicionais = adicionadas.value)
            }
        }
    }
}

fun parseStringInt(x: String): Int? {
    try {
        when{
            x.isDigitsOnly() ->
                if("^0{2,}".toRegex().matches(x)) return x.replace("00","0").toInt()
                else return x.toInt()
            else -> return null
        }
    } catch (e: java.lang.RuntimeException) { return null}
}

fun parseStringToIntInput(x: String, y: MutableState<Int>): String {
    val pRes = parseStringInt(x) ?: ""
    if (pRes is Int){
        y.value = pRes
        if (pRes > 5000) return "Limite Atingido!"
    }
    else if (pRes is String && pRes.isEmpty()) {
        y.value = 0
        return ""
    }

    return y.value.toString()
}

@Composable
fun InputValues(valor: MutableState<Int>, modifier: Modifier = Modifier, labelText: String = ""){
    val displayVal = remember { mutableStateOf("")}
    TextField(
        value = displayVal.value,
        onValueChange = { v -> displayVal.value = parseStringToIntInput(v, valor) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(text = labelText) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface,
            textColor = Color.Magenta,
        ),
        modifier = modifier
            .height(65.dp)
            .fillMaxWidth()
    )
}

@Composable
fun PossibleValues(valorDesejado: Int, sobras: Int, adicionais: Int){
    Column() {
        // s = sobras
        // a = adicionais
        // d = valorDesejado
        // displayList = {i|∀i ∈ {floor(d / 2) ... d+100} | i mod 32 == s + a }
        // displayList_n = displayList.length - 5 == last 5 elements.
        // Get the index of the smallest value.
        // closestValue = i|∀i=n-5 ∈ displaylist_n||d - displayList_i|| min displayList_i
        // Check if is the minimum value of the 5 elements in given list.
        // isMinFound = |∀i=n-5 ∈ displaylist_n|displayList_i == displayList_closestValue
        var copySobras = sobras - adicionais
        // Protects from break the text display.
        if (copySobras.absoluteValue > valorDesejado) copySobras = 0
        while(copySobras < 0){copySobras += 32}
        while(copySobras > 31){copySobras -= 32}

        val displayList: MutableList<Int> = mutableListOf()
        for (valor in valorDesejado.floorDiv(2)..valorDesejado + 100){
            if (valor % 32 == copySobras){
                displayList.add(valor)
            }
        }
        val closestValue = displayList.takeLast(5).map { it -> (valorDesejado - it).absoluteValue }.withIndex().minBy { (_, v) -> v }.index
        Spacer(modifier = Modifier.padding(top = 50.dp))
        for (valor in displayList.takeLast(6)){
                Card(modifier = Modifier
                    .padding(bottom = 5.dp)
                    .fillMaxWidth()
                    .size(30.dp)
                ){
                    val slings = (valor / 32)
                    if (valor == displayList.takeLast(5).get(closestValue)){
                        Text(text = "Slings: $slings       Total: $valor",
                            color = Color.Magenta, textAlign = TextAlign.Start,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 40.dp)
                        )
                    } else {
                        Text(text = "Slings: $slings       Total: $valor",
                            textAlign = TextAlign.Start,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 40.dp)
                        )
                    }
            }

            }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Apontamento_AmspecTheme {
        Apontador()
    }
}