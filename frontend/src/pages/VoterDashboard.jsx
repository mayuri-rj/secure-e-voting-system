import { useEffect, useState } from "react";
import axios from "axios";

function VoterDashboard() {

  const [elections, setElections] = useState([]);
  const [hasVoted, setHasVoted] = useState(false);

  useEffect(() => {
    fetchElections();
  }, []);

  const fetchElections = async () => {
    try {
      const res = await axios.get("http://localhost:8080/elections/all", {
        headers: {
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
      });

      setElections(res.data);
    } catch (error) {
      console.error("Error fetching elections", error);
    }
  };
  const castVote = async (electionId, candidateId) => {
  try {

    const response = await axios.post(
      "http://localhost:8080/vote/cast",
      {
        electionId: electionId,
        candidateId: candidateId
      },
      {
        headers: {
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
      }
    );

    setHasVoted(true);
    alert(response.data);
    fetchElections();

  } catch (error) {
    alert(error.response?.data || "Voting failed");
  }
};

  return (
  <div className="p-10">
    <h1 className="text-3xl font-bold mb-6">Voter Dashboard</h1>

    {elections
      .filter((e) => e.status === "ACTIVE")
      .map((election) => (
        <div key={election.id} className="border p-4 mb-6 rounded">

          <h2 className="text-xl font-semibold mb-3">
            {election.title}
          </h2>

          {election.candidates && election.candidates.length > 0 ? (
            election.candidates.map((candidate) => (
              <div
                key={candidate.id}
                className="flex justify-between items-center border p-2 mb-2 rounded"
              >
                <span>
                  {candidate.name} - {candidate.party}
                </span>

                <button
                  disabled={hasVoted}
                  onClick={() => castVote(election.id, candidate.id)}
                  className="bg-green-600 text-white px-3 py-1 rounded disabled:bg-gray-400"
                >
                  {hasVoted ? "Already Voted" : "Vote"}
                </button>
              </div>
            ))
          ) : (
            <p className="text-gray-500">No candidates</p>
          )}

        </div>
      ))}
  </div>
);
}

export default VoterDashboard;