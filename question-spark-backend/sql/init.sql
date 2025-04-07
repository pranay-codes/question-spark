CREATE TABLE profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    username TEXT UNIQUE NOT NULL,
    full_name TEXT,
    avatar_url TEXT,
    preferred_language TEXT DEFAULT 'en',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);


CREATE TABLE stories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    initial_prompt TEXT NOT NULL,
    author_id UUID REFERENCES profiles (id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE story_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    story_id UUID REFERENCES stories(id) ON DELETE CASCADE,
    parent_question_id UUID REFERENCES story_questions (id) ON DELETE SET NULL,
    question_type VARCHAR(50) NOT NULL,  -- 'text', 'voice', 'input', etc.
    question_text JSONB,         -- The text of the question
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE story_narrative (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    story_id UUID REFERENCES stories(id) ON DELETE CASCADE,
    user_id UUID REFERENCES profiles (id) ON DELETE CASCADE,
    parent_narrative_id UUID REFERENCES story_narrative(id) ON DELETE SET NULL,
    question_id UUID REFERENCES story_questions(id) ON DELETE CASCADE,  -- Foreign key to story_questions table
    choice_text TEXT NOT NULL,           -- Available choice text presented to the user
    response_text TEXT NOT NULL,         -- The system's response after the user makes a choice
    next_narrative JSONB,                  -- JSON structure defining branching choices
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE user_story_progress (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES profiles (id) ON DELETE CASCADE,
    story_id UUID REFERENCES stories(id) ON DELETE CASCADE,
    current_narrative_id UUID REFERENCES story_narrative(id) ON DELETE CASCADE,
    progress_path JSONB NOT NULL, -- Stores the sequence of choices made
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);




CREATE TABLE cached_story_prompts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    story_id UUID REFERENCES stories(id) ON DELETE CASCADE,
    prompt_text TEXT NOT NULL,
    response_options JSONB NOT NULL, -- Pre-generated responses for quick retrieval
    expires_at TIMESTAMP NOT NULL, -- TTL for caching
    created_at TIMESTAMP DEFAULT NOW()
);
s