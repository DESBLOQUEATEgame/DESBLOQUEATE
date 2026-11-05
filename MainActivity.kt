package com.desbloqueate.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

private val bg = Color(0xFF080018)
private val neon = Color(0xFF00F5FF)
private val pink = Color(0xFFFF35C8)
private const val W = 10
private const val H = 18

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GameScreen() }
    }
}

@Composable
fun GameScreen() {
    var board by remember { mutableStateOf(List(H) { List(W) { 0 } }) }
    var score by remember { mutableIntStateOf(0) }
    var pieceX by remember { mutableIntStateOf(4) }
    var pieceY by remember { mutableIntStateOf(0) }
    var paused by remember { mutableStateOf(false) }
    val piece = remember { listOf(Offset(0f,0f), Offset(1f,0f), Offset(0f,1f), Offset(1f,1f)) }
    val colors = listOf(Color.Transparent, neon, pink, Color(0xFFB45CFF), Color(0xFFFFD166))
    fun reset() { board = List(H) { List(W) { 0 } }; score = 0; pieceX = 4; pieceY = 0; paused = false }
    fun move(dx: Int) { if (!paused && pieceX + dx in 0..(W-2)) pieceX += dx }
    fun drop() {
        if (paused) return
        val nextY = (pieceY + 1).coerceAtMost(H-2)
        if (nextY == H-2) {
            val b = board.map { it.toMutableList() }.toMutableList()
            val c = Random.nextInt(1, colors.size)
            piece.forEach { p -> b[pieceY.toIntSafe() + p.y.toInt()][(pieceX + p.x.toInt()).coerceIn(0,W-1)] = c }
            val kept = b.filter { row -> row.any { it == 0 } }
            val cleared = H - kept.size
            board = List(cleared) { List(W) { 0 } } + kept
            score += if (cleared > 0) cleared * 100 else 10
            pieceX = 4; pieceY = 0
        } else pieceY = nextY
    }
    Column(Modifier.fillMaxSize().background(bg).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("DESBLOQUÉATE", color = neon, fontSize = 27.sp)
        Text("ROMPE EL BLOQUEO", color = pink, fontSize = 12.sp)
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("PUNTOS  $score", color = Color.White)
            Text(if (paused) "PAUSA" else "NIVEL 1", color = neon)
        }
        Spacer(Modifier.height(12.dp))
        Canvas(Modifier.fillMaxWidth().weight(1f).border(1.dp, neon, RoundedCornerShape(8.dp)).padding(4.dp)) {
            val cw = size.width / W
            val ch = size.height / H
            for (y in 0 until H) for (x in 0 until W) {
                if (board[y][x] != 0) drawRect(colors[board[y][x]], Offset(x*cw, y*ch), androidx.compose.ui.geometry.Size(cw-2, ch-2))
            }
            if (!paused) piece.forEach { p ->
                val x = pieceX + p.x.toInt(); val y = pieceY + p.y.toInt()
                drawRect(pink, Offset(x*cw, y*ch), androidx.compose.ui.geometry.Size(cw-2, ch-2))
            }
            for (x in 1 until W) drawLine(Color(0x3322FFFF), Offset(x*cw,0f), Offset(x*cw,size.height), 1f)
            for (y in 1 until H) drawLine(Color(0x3322FFFF), Offset(0f,y*ch), Offset(size.width,y*ch), 1f)
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { move(-1) }) { Text("◀") }
            Button(onClick = { drop() }) { Text("▼") }
            Button(onClick = { move(1) }) { Text("▶") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { paused = !paused }) { Text(if (paused) "CONTINUAR" else "PAUSA") }
            OutlinedButton(onClick = { reset() }) { Text("REINICIAR") }
        }
        Text("Versión inicial de prueba · 0.1.0", color = Color.LightGray, fontSize = 11.sp)
    }
}

private fun Int.toIntSafe() = this.coerceIn(0, H-1)
