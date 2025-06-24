const model = 'llama3.2:1b';
const systemPrompt = `You are a travel assistant created by Share Trip. Your role is to provide clear, direct, and helpful responses about Points of Interest (POIs), travel destinations, maps, cultural information, routes, and general travel-related topics around the world.

Always focus on travel, places, landmarks, countries, and culture. Avoid deviating into unrelated topics.

Be professional, informative, and concise. Format your responses in a way that is easy to read and helpful for users planning trips or learning about the world.`;

const systemPromptSummarize = `
You are a summarizer for a travel assistant conversation. Your task is to summarize the following conversation clearly, accurately, and concisely.

Use this exact format:
User: [summary of user message]
Assistant: [summary of assistant response]
(Repeat as needed)

Instructions:
- Only include information explicitly mentioned in the conversation.
- Do not assume or invent details (e.g. don't assume the user wants to visit Japan if they didn't say so).
- Do not translate place names unless the assistant did so.
- You may omit short greetings or confirmations (e.g. “Boas”, “Ok”) unless they are meaningful.
- Ensure summaries are consistent with the theme: travel, places, points of interest, maps, countries, or cultures.
`;


async function askChatbot(prompt, context) {
    let summarizedConversation;

    if (context) {
        const fullPromptSummarize = `${systemPromptSummarize}\n\nConversation: ${context}\nSummary:`;
        summarizedConversation = await fetchResponse(fullPromptSummarize, model);

        console.log(
            "====================================\n"
            + fullPromptSummarize + "\n\n"
            + summarizedConversation + "\n"
            + "===================================="
        );
    }

    if (summarizedConversation) {
        const fullPromptContext = `${systemPrompt}\n\nContext: ${summarizedConversation}\n\nUser: ${prompt}\nAssistant:`;
        return await fetchResponse(fullPromptContext, model);
    } else {
        const fullPrompt = `${systemPrompt}\n\nUser: ${prompt}\nAssistant:`;
        return await fetchResponse(fullPrompt, model);
    }
}

async function fetchResponse(prompt, model) {
    try {
        const response = await fetch('http://localhost:11434/api/generate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                model: model,
                prompt: prompt,
                stream: false
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const responseText = await response.text();
        const data = JSON.parse(responseText);
        return data.response;

    } catch (error) {
        console.error('Erro ao contactar o Ollama:', error);
        return null;
    }
}

module.exports = { askChatbot };
