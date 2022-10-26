package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristianovecchi.mikrokanon.ui.AppColors

//@Preview
//@Composable
//fun CustomButtonPreview(){
//    CustomButton(
//        colors = AppColors.getCustomColorsByColorArrays(57),
//        text = "->"
//    ) {
//
//    }
//}
//@Preview
//@Composable
//fun ButtonPreview(){
//    Button(
//        colors = ButtonDefaults.outlinedButtonColors(
//            backgroundColor = Color.Blue
//        ),
//        onClick = {},
//        border = BorderStroke(1.dp, Color.Red),
//        shape = RectangleShape) {
//        Text(text = "Rectangle shape",
//            style = TextStyle(
//                color = Color.White,
//                fontSize = 8.sp,
//                fontWeight = FontWeight.Bold
//            ),
//            )
//    }
//}
@Preview
@Composable
fun TextPreview(){
    Column{
        Button(
            modifier = Modifier.height(30.dp)
                    ,
            onClick = {}
        ){
            Text(
                text = "SemicolonSpace",
                modifier = Modifier
                    //.padding(top = 32.dp) // margin
                    .background(color = Color.Yellow)
                //.padding(top = 16.dp) // padding
            )
        }

        Text(
            text = "SemicolonSpace",
            modifier = Modifier
                //.padding(top = 32.dp) // margin
                .background(color = Color.Yellow)
            //.padding(top = 16.dp) // padding
        )
    }

}