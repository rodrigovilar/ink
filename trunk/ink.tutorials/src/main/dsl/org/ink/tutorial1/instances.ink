Object id="Student_Offers_Template_For_2010" class="ink.tutorial1:AbstractOffer" {
 studentOnlyOffer true
 renewalOnlyOffer false
 //validUntil "2010-11-01" 
} 


Object id="students_30_percent_discount_for_1_year" super="ink.tutorial1:Student_Offers_Template_For_2010" class="ink.tutorial1:FixedPercentageDiscountOffer"{
 percentage 30.0
 conditionForPeriodsSigned 1
}

Object id="students_50_percent_discount_for_2_years" super="ink.tutorial1:Student_Offers_Template_For_2010" class="ink.tutorial1:FixedPercentageDiscountOffer"{
 percentage 50.0
 conditionForPeriodsSigned 2
}

Object id="students_60_percent_discount_for_3_years" super="ink.tutorial1:Student_Offers_Template_For_2010" class="ink.tutorial1:FixedPercentageDiscountOffer"{
 percentage 60.0
 conditionForPeriodsSigned 3
}
Object id="Active_offers" class="ink.tutorial1:ActiveOffers" {
	offers{
		offer ref="ink.tutorial1:students_30_percent_discount_for_1_year"
		offer ref="ink.tutorial1:students_50_percent_discount_for_2_years"
		offer ref="ink.tutorial1:students_60_percent_discount_for_3_years"
	}
}