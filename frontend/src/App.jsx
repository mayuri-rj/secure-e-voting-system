import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import VoterDashboard from "./pages/VoterDashboard";
import Admin from "./pages/Admin";


function App() {
  return (
    <Routes>
    <Route path="/" element={<Login />} />
    <Route path="/login" element={<Login />} />
    <Route path="/register" element={<Register />} />
    <Route path="/voter-dashboard" element={<VoterDashboard />} />
    <Route path="/admin" element={<Admin />} />
  </Routes>
  );
}

export default App;