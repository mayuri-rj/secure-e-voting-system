import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";


function Admin() {
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [message, setMessage] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [elections, setElections] = useState([]);
  const [candidateName, setCandidateName] = useState("");
  const [candidateParty, setCandidateParty] = useState("");
  const [selectedElection, setSelectedElection] = useState("");
  const [results, setResults] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");

    if (!token || role !== "ADMIN") {
      navigate("/login");
    }
  }, [navigate]);

  useEffect(() => {
  fetchElections();
  const interval = setInterval(fetchElections, 30000); // refresh every 30s
  return () => clearInterval(interval);
}, []);
   


//for fetching elections to show Existing Elections.
const fetchElections = async () => {
  try {
    const res = await axios.get("http://localhost:8080/elections/all", {
      headers: {
        Authorization: "Bearer " + localStorage.getItem("token"),
      },
    });
    setElections(res.data);
  } catch (err) {
    console.error("Error fetching elections", err);
  }
};


  const handleCreateElection = async (e) => {
    e.preventDefault();
    setMessage("");

    try {
      const response = await fetch("http://localhost:8080/elections/create", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
        body: JSON.stringify({ 
          title,
          startDate,
          endDate
         }),
      });

      if (!response.ok) {
        throw new Error("Failed to create election");
      }

     setMessage("Election created successfully!");
setTitle("");
setStartDate("");
setEndDate("");

fetchElections(); // ⭐ refresh elections list

    } catch (err) {
      setMessage("Error creating election");
    }
  };

// FOR ADDING CANDIDATES
  const handleAddCandidate = async (e) => {
  e.preventDefault();

  try {
    const response = await fetch(
      `http://localhost:8080/candidates/create/${selectedElection}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
        name: candidateName
    }), 
      }
    );

    if (response.ok) {
      setMessage("Candidate added successfully");
      fetchElections(); // refresh list
    } else {
      setMessage("Error adding candidate");
    }

  } catch (error) {
    console.error(error);
    setMessage("Error adding candidate");
  }
};

 const fetchResults = async (electionId) => {
  try {
    const response = await axios.get(
      `http://localhost:8080/elections/results/${electionId}`
    );

    setResults(response.data);
  } catch (error) {
    console.error("Error fetching results", error);
  }
};

  return (
  <div className="p-10">
    <h1 className="text-3xl font-bold mb-6 text-red-700">
      Admin Dashboard
    </h1>

    {/* --- Create Election Form --- */}
    <form onSubmit={handleCreateElection} className="space-y-4 max-w-md">
      <input
        type="text"
        placeholder="Enter Election Title"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        className="w-full border px-4 py-2 rounded"
        required
      />
      <input
        type="datetime-local"
        value={startDate}
        onChange={(e) => setStartDate(e.target.value)}
        className="w-full border px-4 py-2 rounded"
        required
      />
      <input
        type="datetime-local"
        value={endDate}
        onChange={(e) => setEndDate(e.target.value)}
        className="w-full border px-4 py-2 rounded"
        required
      />

      <button
        type="submit"
        className="bg-red-600 text-white px-4 py-2 rounded"
      >
        Create Election
      </button>

      <h2 className="text-xl mt-10">Existing Elections</h2>
      <ul className="space-y-2">
  {elections.map((election) => (
    <li key={election.id} className="border p-2 rounded">
      <strong>{election.title}</strong> - {election.status}
      {election.candidates && election.candidates.length > 0 ? (
        <ul className="ml-6 mt-1 list-disc">
          {election.candidates.map((cand) => (
            <li key={cand.id}>
              {cand.name} - {cand.party}
            </li>
          ))}
        </ul>
      ) : (
        <p className="ml-6 mt-1 italic text-gray-500">No candidates yet</p>
      )}
    </li>
  ))}
</ul>
    </form>

    {/* --- Add Candidate Form --- */}
    <div className="mt-10 max-w-md">
      <h2 className="text-xl mb-4">Add Candidate</h2>
      <form onSubmit={handleAddCandidate} className="space-y-4">
        <input
          type="text"
          placeholder="Candidate Name"
          value={candidateName}
          onChange={(e) => setCandidateName(e.target.value)}
          className="w-full border px-4 py-2 rounded"
          required
        />
      
        <select
          value={selectedElection}
          onChange={(e) => setSelectedElection(e.target.value)}
          className="w-full border px-4 py-2 rounded"
          required
        >
          <option value="">Select Election</option>
          {elections.map((e) => (
            <option key={e.id} value={e.id}>
              {e.title} - {e.status}
            </option>
          ))}
        </select>
        <button
          type="submit"
          className="bg-blue-600 text-white px-4 py-2 rounded"
        >
          Add Candidate
        </button>

       <ul className="space-y-2">
  {elections.map((election) => (
    <li key={election.id} className="border p-2 rounded">
      <strong>{election.title}</strong> - {election.status}

      <button
        onClick={() => fetchResults(election.id)}
        className="ml-4 bg-blue-500 text-white px-2 py-1 rounded"
      >
        View Results
      </button>

      {election.candidates && election.candidates.length > 0 ? (
        <ul className="ml-6 mt-1 list-disc">
          {election.candidates.map((cand) => (
            <li key={cand.id}>
              {cand.name} - {cand.party}
            </li>
          ))}
        </ul>
      ) : (
        <p className="ml-6 mt-1 italic text-gray-500">No candidates yet</p>
      )}
    </li>
  ))}
</ul>
      </form>
    </div>

 {elections.status === "COMPLETED" && election.candidates && (
  <div className="mt-2 text-green-700">
    <strong>Results:</strong>
    <ul className="ml-4 list-disc">
      {elections.candidates
        .sort((a, b) => b.vote_count - a.voteCount)
        .map((candidate) => (
          <li key={candidate.id}>
            {candidate.name} ({candidate.party}) - {candidate.voteCount} votes
          </li>
      ))}
    </ul>
  </div>
)}

    {message && <p className="mt-4">{message}</p>}

    {results && (
  <div className="mt-8 p-4 border rounded bg-gray-100">
    <h2 className="text-xl font-bold mb-2">
      {results.title} Results
    </h2>

    <ul className="list-disc ml-6">
      {results.candidates.map((c) => (
        <li key={c.id}>
          {c.name} - {c.voteCount} votes
        </li>
      ))}
    </ul>

    {results.winner && (
      <p className="mt-3 text-green-700 font-bold">
        Winner: {results.winner.name}
      </p>
    )}
  </div>
)}
  </div>
);

}

export default Admin;