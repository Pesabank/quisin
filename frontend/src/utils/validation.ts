import { z } from 'zod'

export const emailSchema = z
  .string()
  .email('Invalid email format')
  .min(1, 'Email is required')

export const passwordSchema = z
  .string()
  .min(8, 'Password must be at least 8 characters')
  .regex(/[A-Z]/, 'Password must contain at least one uppercase letter')
  .regex(/[a-z]/, 'Password must contain at least one lowercase letter')
  .regex(/[0-9]/, 'Password must contain at least one number')

export const phoneSchema = z
  .string()
  .regex(/^\+?[1-9]\d{1,14}$/, 'Invalid phone number format')

export const validateForm = async (schema: z.ZodType<any>, data: any) => {
  try {
    await schema.parseAsync(data)
    return { success: true, errors: null }
  } catch (error) {
    if (error instanceof z.ZodError) {
      const errors = error.errors.reduce((acc: Record<string, string>, curr) => {
        const path = curr.path.join('.')
        acc[path] = curr.message
        return acc
      }, {})
      return { success: false, errors }
    }
    return { success: false, errors: { _form: 'Validation failed' } }
  }
}
