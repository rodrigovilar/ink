Object id="Student_Offers_Template_For_2010" class="ink.tutorial1:BaseOffer" abstract=true {
 studentOnlyOffer true
 renewalOnlyOffer false
 validUntil 2011/11/01 
} 


Object id="students_30_percent_discount_for_1_year" super="Student_Offers_Template_For_2010" class="ink.tutorial1:FixedPercentageDiscountOffer"{
 percentage 30.0
 conditionForPeriodsSigned 1
}

Object id="students_50_percent_discount_for_2_years" super="Student_Offers_Template_For_2010" class="ink.tutorial1:FixedPercentageDiscountOffer"{
 percentage 50.0
 conditionForPeriodsSigned 2
 freeIssues 2
}

Object id="students_60_percent_discount_for_3_years" super="Student_Offers_Template_For_2010" class="ink.tutorial1:FixedPercentageDiscountOffer"{
 percentage 60.0
 conditionForPeriodsSigned 3
 freeIssues 3
}
Object id="Active_offers" class="ActiveOffers" {
	offers{
		offer ref="students_30_percent_discount_for_1_year"
		offer ref="students_50_percent_discount_for_2_years"
		offer ref="students_60_percent_discount_for_3_years"
	}
}