Object id="Customer1" class="Customer" {
	name "Peter Parker"
	age 18
}

Object id="Customer2" class="Customer" {
	name "John Connor"
	age 33
}

Object id="TerminatorTape1" class="Terminator" {
	isRented false
}

Object id="TerminatorTape2" class="Terminator" {
	isRented true
	renter ref="Customer1"
}

Object id="SpidermanTape1" class="Spiderman" {
	isRented true
	renter ref="Customer1"
	hasSubtitles true
}

Object id="KillBillTape1" class="KillBill" {
	isRented false
}