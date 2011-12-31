Object id="Student_Offers_Template_For_2010" class="ink.tutorial2:BaseOffer" abstract=true {
 studentOnlyOffer true
 renewalOnlyOffer false
 validUntil 2013/11/01 
 registrationFormType ref="basic_registration_form"
} 


Object id="students_30_percent_discount_for_1_year" super="Student_Offers_Template_For_2010" class="ink.tutorial2:PercentageDiscountOffer"{
 percentage 30.0
 conditionForPeriodsSigned 1
}

Object id="students_50_percent_discount_for_2_years" super="Student_Offers_Template_For_2010" class="ink.tutorial2:PercentageDiscountOffer"{
 percentage 50.0
 conditionForPeriodsSigned 2
 freeIssues 2
}

Object id="ExampleOffer" class="PercentageDiscountOffer" {
	percentage 20.0
	studentOnlyOffer true
	validUntil 2013/11/01 
	renewalOnlyOffer false
	freeIssues 0
}

Class id="basic_registration_form" class="MetaRegistrationForm" super="BaseRegistrationForm" {
	java_path ""
	java_mapping "No_Java"
	author "Atzmon"
	properties{
		property class="ink.core:StringAttribute"{
			name "firstName"
			display_name "First Name"
		}
		property class="ink.core:StringAttribute"{
			name "lastName"
			display_name "Last Name"
		}
		property class="ink.core:StringAttribute"{
			name "email"
			display_name "Email address"
		}
	}
}

Class id="high_value_registration_form" class="MetaRegistrationForm" super="basic_registration_form" {
	java_path ""
	java_mapping "No_Java"
	properties{
		property class="ink.core:BooleanAttribute"{
			name "optIn"
			display_name "Can we send special offers to your email?"
			mandatory true
		}
	}
}

Object id="students_60_percent_discount_for_3_years" super="Student_Offers_Template_For_2010" class="ink.tutorial2:PercentageDiscountOffer"{
 percentage 60.0
 conditionForPeriodsSigned 3
 freeIssues 3
 registrationFormType ref="high_value_registration_form"
}

Object id="Student1_registration_form" class="basic_registration_form" {
	firstName "Lior"
	lastName "Schachter"
	email "lior@ink.org"
}

Object id="Student2_registration_form" class="high_value_registration_form" super="Student1_registration_form" {
	optIn true
}

Object id="Active_offers" class="ActiveOffers" {
	offers{
		offer ref="students_30_percent_discount_for_1_year"
		offer ref="students_50_percent_discount_for_2_years"
		offer ref="students_60_percent_discount_for_3_years"
	}
}

