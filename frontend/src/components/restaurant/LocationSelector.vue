&lt;template&gt;
  &lt;div class="space-y-4"&gt;
    &lt;div&gt;
      &lt;label class="block text-sm font-medium text-gray-700"&gt;Country&lt;/label&gt;
      &lt;select
        v-model="selectedCountry"
        @change="onCountryChange"
        class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
      &gt;
        &lt;option value=""&gt;Select a country&lt;/option&gt;
        &lt;option v-for="country in countries" :key="country" :value="country"&gt;
          {{ country }}
        &lt;/option&gt;
      &lt;/select&gt;
    &lt;/div&gt;

    &lt;div v-if="selectedCountry"&gt;
      &lt;label class="block text-sm font-medium text-gray-700"&gt;City&lt;/label&gt;
      &lt;select
        v-model="selectedCity"
        @change="onCityChange"
        class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
      &gt;
        &lt;option value=""&gt;Select a city&lt;/option&gt;
        &lt;option v-for="city in cities" :key="city" :value="city"&gt;
          {{ city }}
        &lt;/option&gt;
      &lt;/select&gt;
    &lt;/div&gt;

    &lt;div v-if="selectedCity"&gt;
      &lt;label class="block text-sm font-medium text-gray-700"&gt;Area&lt;/label&gt;
      &lt;select
        v-model="selectedArea"
        @change="onAreaChange"
        class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
      &gt;
        &lt;option value=""&gt;Select an area&lt;/option&gt;
        &lt;option v-for="area in areas" :key="area" :value="area"&gt;
          {{ area }}
        &lt;/option&gt;
      &lt;/select&gt;
    &lt;/div&gt;

    &lt;div v-if="error" class="mt-2 text-sm text-red-600"&gt;
      {{ error }}
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'

const props = defineProps&lt;{
  modelValue?: {
    country: string
    city: string
    area: string
  }
}&gt;()

const emit = defineEmits(['update:modelValue', 'location-changed'])

const countries = ref&lt;string[]&gt;([])
const cities = ref&lt;string[]&gt;([])
const areas = ref&lt;string[]&gt;([])
const error = ref('')

const selectedCountry = ref('')
const selectedCity = ref('')
const selectedArea = ref('')

// Watch for external value changes
watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    selectedCountry.value = newValue.country
    selectedCity.value = newValue.city
    selectedArea.value = newValue.area
  }
}, { deep: true })

const fetchCountries = async () => {
  try {
    const response = await axios.get('/api/locations/countries')
    countries.value = response.data
  } catch (err) {
    error.value = 'Failed to fetch countries'
    console.error('Error fetching countries:', err)
  }
}

const fetchCities = async (country: string) => {
  try {
    const response = await axios.get(`/api/locations/cities/${country}`)
    cities.value = response.data
  } catch (err) {
    error.value = 'Failed to fetch cities'
    console.error('Error fetching cities:', err)
  }
}

const fetchAreas = async (country: string, city: string) => {
  try {
    const response = await axios.get(`/api/locations/areas/${country}/${city}`)
    areas.value = response.data
  } catch (err) {
    error.value = 'Failed to fetch areas'
    console.error('Error fetching areas:', err)
  }
}

const onCountryChange = async () => {
  selectedCity.value = ''
  selectedArea.value = ''
  cities.value = []
  areas.value = []
  
  if (selectedCountry.value) {
    await fetchCities(selectedCountry.value)
  }
  
  updateValue()
}

const onCityChange = async () => {
  selectedArea.value = ''
  areas.value = []
  
  if (selectedCountry.value && selectedCity.value) {
    await fetchAreas(selectedCountry.value, selectedCity.value)
  }
  
  updateValue()
}

const onAreaChange = () => {
  updateValue()
}

const updateValue = () => {
  const value = {
    country: selectedCountry.value,
    city: selectedCity.value,
    area: selectedArea.value
  }
  
  emit('update:modelValue', value)
  emit('location-changed', value)
}

onMounted(async () => {
  await fetchCountries()
  
  if (props.modelValue?.country) {
    selectedCountry.value = props.modelValue.country
    await fetchCities(props.modelValue.country)
    
    if (props.modelValue?.city) {
      selectedCity.value = props.modelValue.city
      await fetchAreas(props.modelValue.country, props.modelValue.city)
      
      if (props.modelValue?.area) {
        selectedArea.value = props.modelValue.area
      }
    }
  }
})
&lt;/script&gt;
