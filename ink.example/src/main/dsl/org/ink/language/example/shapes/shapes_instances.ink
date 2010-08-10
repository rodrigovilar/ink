Object id="myFirstShape" class="example.shapes:Shape" {
	x  100
	y  100
}

Object id="myFirstShape2" class="example.shapes:Shape" super="example.shapes:myFirstShape" {
	x  200
}

Object id="drawing1" class="example.shapes:Drawing" {
	elements {
		element ref="example.shapes:myFirstShape"
		element ref="example.shapes:myFirstShape2"
	}
}		