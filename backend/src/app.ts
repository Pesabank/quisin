import express from 'express'
import cors from 'cors'
import authRouter from './routes/auth'
import tablesRouter from './routes/tables'
import inventoryRouter from './routes/inventory'

const app = express()

app.use(cors())
app.use(express.json())

// Routes
app.use('/api/auth', authRouter)
app.use('/api/tables', tablesRouter)
app.use('/api/inventory', inventoryRouter)

export default app 