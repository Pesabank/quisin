import mongoose from 'mongoose'

const locationSchema = new mongoose.Schema({
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
  latitude: {
    type: Number,
    required: true
  },
  longitude: {
    type: Number,
    required: true
  },
  isActive: {
    type: Boolean,
    default: true
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

locationSchema.pre('save', function(next) {
  this.updatedAt = new Date()
  next()
})

// Create compound indexes for efficient queries
locationSchema.index({ country: 1, city: 1, area: 1 })
locationSchema.index({ latitude: 1, longitude: 1 })

export const Location = mongoose.model('Location', locationSchema)
