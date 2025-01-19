import mongoose from 'mongoose'

const restaurantSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
    trim: true
  },
  description: {
    type: String,
    required: true
  },
  isChain: {
    type: Boolean,
    default: false
  },
  chainInfo: {
    parentRestaurant: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Restaurant'
    },
    menuType: {
      type: String,
      enum: ['shared', 'custom', 'independent'],
      default: 'independent'
    },
    branches: [{
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Restaurant'
    }]
  },
  currency: {
    code: {
      type: String,
      required: true,
      default: 'USD'
    },
    symbol: {
      type: String,
      required: true,
      default: '$'
    },
    name: {
      type: String,
      required: true,
      default: 'US Dollar'
    }
  },
  location: {
    country: {
      type: String,
      required: true,
      index: true
    },
    city: {
      type: String,
      required: true,
      index: true
    },
    area: {
      type: String,
      required: true,
      index: true
    },
    address: {
      type: String,
      required: true
    },
    latitude: {
      type: Number,
      required: true
    },
    longitude: {
      type: Number,
      required: true
    }
  },
  cuisine: [{
    type: String,
    required: true
  }],
  priceRange: {
    type: String,
    enum: ['$', '$$', '$$$', '$$$$'],
    required: true
  },
  openingHours: {
    monday: { open: String, close: String },
    tuesday: { open: String, close: String },
    wednesday: { open: String, close: String },
    thursday: { open: String, close: String },
    friday: { open: String, close: String },
    saturday: { open: String, close: String },
    sunday: { open: String, close: String }
  },
  contactInfo: {
    phone: String,
    email: String,
    website: String
  },
  features: [{
    type: String,
    enum: ['Parking', 'Outdoor Seating', 'Wifi', 'Delivery', 'Takeout', 'Reservations']
  }],
  images: [{
    url: String,
    caption: String
  }],
  rating: {
    average: {
      type: Number,
      default: 0
    },
    count: {
      type: Number,
      default: 0
    }
  },
  isActive: {
    type: Boolean,
    default: true
  },
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
})

// Create compound indexes for efficient queries
restaurantSchema.index({ 'location.country': 1, 'location.city': 1, 'location.area': 1 })
restaurantSchema.index({ 'location.latitude': 1, 'location.longitude': 1 })
restaurantSchema.index({ cuisine: 1 })
restaurantSchema.index({ priceRange: 1 })
restaurantSchema.index({ 'rating.average': -1 })
restaurantSchema.index({ isChain: 1, 'chainInfo.parentRestaurant': 1 })

restaurantSchema.pre('save', function(next) {
  this.updatedAt = new Date()
  next()
})

export const Restaurant = mongoose.model('Restaurant', restaurantSchema)
