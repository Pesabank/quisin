import mongoose, { Document, Schema } from 'mongoose'

export interface ILoyaltySettings extends Document {
  enabled: boolean
  pointsPerDollar: number
  tierThresholds: {
    silver: number
    gold: number
    platinum: number
  }
  updatedAt: Date
}

const loyaltySettingsSchema = new Schema({
  enabled: {
    type: Boolean,
    default: false
  },
  pointsPerDollar: {
    type: Number,
    default: 1,
    min: 0
  },
  tierThresholds: {
    silver: {
      type: Number,
      default: 1000,
      min: 0
    },
    gold: {
      type: Number,
      default: 5000,
      min: 0
    },
    platinum: {
      type: Number,
      default: 10000,
      min: 0
    }
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
})

loyaltySettingsSchema.pre('save', function(next) {
  this.updatedAt = new Date()
  next()
})

export const LoyaltySettings = mongoose.model<ILoyaltySettings>('LoyaltySettings', loyaltySettingsSchema)
