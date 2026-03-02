import { HashRouter, Route, Routes } from 'react-router'
import './App.css'
import LoginPage from './pages/LoginPage'
import HomePage from './pages/HomePage'
import SignupPage from './pages/SignupPage'

function App() {
  return (
    <HashRouter>
      <Routes>
        <Route path = "/" element={<LoginPage/>}/> 
        <Route path = "/login" element = {<LoginPage/>}/>
        <Route path = "/signup" element = {<SignupPage/>}/>
        <Route path = "/home" element = {<HomePage/>}/>
      </Routes>
    </HashRouter>
  )
}

export default App
