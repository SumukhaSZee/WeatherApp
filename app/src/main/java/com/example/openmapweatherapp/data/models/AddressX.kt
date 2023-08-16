package com.example.openmapweatherapp.data.models

data class AddressX(
    val city: List<City>,
    val countryCode: List<CountryCode>,
    val district: List<District>,
    val label: List<Label>
)