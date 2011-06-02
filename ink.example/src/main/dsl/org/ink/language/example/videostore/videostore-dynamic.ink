Class id="Terminator" class="Movie" super="Videotape" {
	java_path ""
	java_mapping "No_Java"
	component_type "Root"
	rating "R"
	title "The Terminator (1984)"
}

Class id="Spiderman" class="Movie" super="Videotape" {
	java_path ""
	java_mapping "No_Java"
	component_type "Root"
	rating "PG-13"
	title "Spider-Man (2002)"
	properties {
		property class="ink.core:BooleanAttribute" {
			name "hasSubtitles"
			mandatory true
		}
	}
}

Class id="KillBill" class="RestrictedMovie" super="RestrictedVideotape" {
	java_path ""
	java_mapping "No_Java"
	component_type "Root"
	title "Kill Bill: Vol. 1 (2003)"
	minimumAge 19
}