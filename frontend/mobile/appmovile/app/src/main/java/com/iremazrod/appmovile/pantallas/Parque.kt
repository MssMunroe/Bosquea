package com.iremazrod.appmovile.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iremazrod.appmovile.R

@Composable
fun Parque(nombreParque: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F4E8)) // Tu fondo crema
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título con subrayado elegante (image_a2fcde.png)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = nombreParque,
                fontSize = 32.sp,
                color = Color(0xFF4B3D21), // Marrón ocre
                fontFamily = FontFamily.Serif
            )
            HorizontalDivider(
                modifier = Modifier.width(280.dp),
                thickness = 1.dp,
                color = Color(0xFF4B3D21)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Imagen del Parque
        Image(
            painter = painterResource(id = R.drawable.logo), // Sustituir por tu recurso
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(4.dp)), // Casi cuadrado como en la imagen
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Descripción con estilo justificado
        Text(
            text = "Sus empinadas cimas, a la vez que dominan un inmenso horizonte, matizado de pueblos y de caseríos, con praderas llenas de verdura, están cubiertas de nieve y de hielos...",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Justify,
            color = Color(0xFF2C2C2C)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sección de datos técnicos
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Superficie:", fontWeight = FontWeight.Bold)
            BulletItem("Superficie total: 85.883 ha.")
            BulletItem("Zona periférica de protección: 86.355 ha.")
            BulletItem("Área de influencia socioeconómica: 266.690,91 ha.")

            Spacer(modifier = Modifier.height(8.dp))

            Text(buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Provincia: ") }
                append("Granada y Almería.")
            })
            Text(buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Comunidad Autónoma: ") }
                append("Andalucía.")
            })
        }

        Spacer(modifier = Modifier.height(80.dp)) // Espacio para que el Nav no tape el final
    }
}

@Composable
fun BulletItem(text: String) {
    Row(modifier = Modifier.padding(start = 8.dp, top = 2.dp)) {
        Text(text = "• ", fontWeight = FontWeight.Bold)
        Text(text = text)
    }
}