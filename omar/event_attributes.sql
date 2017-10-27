CREATE TABLE main.events (
    Event_type VARCHAR(255),
    IDESessionUUID VARCHAR(255),
    KaVEVersion VARCHAR(255),
    TriggeredAt TEXT,
    TriggeredBy INTEGER,
    Duration TEXT,
    ActiveWindow VARCHAR(255),
    ActiveDocument VARCHAR(255),
    Window VARCHAR(255),
    Action INTEGER,
    Target TEXT,
    Location TEXT,
    TypeOfNavigation INTEGER,
    Context2 VARCHAR(255),
    NumberOfChanges INTEGER,
    SizeOfChanges INTEGER,
    CommandId VARCHAR(255),
    Mode INTEGER,
    Reason VARCHAR(255),
    Scope VARCHAR(255),
    Targets TEXT, -- JSON
    Document VARCHAR(255),
    Type INTEGER,
    ProposalCollection TEXT, -- JSON
    Selections TEXT, -- JSON
    TerminatedBy INTEGER,
    TerminatedState INTEGER,
    ProposalCount INTEGER,
    Cancelled INTEGER, -- BOOLEAN
    IDELifecyclePhase INTEGER,
    OpenWindows TEXT, -- JSON
    OpenDocuments TEXT, -- JSON
    Actions TEXT, -- JSON
    Solution VARCHAR(255),
    WasAborted INTEGER, -- BOOLEAN
    Tests TEXT, -- JSON
    ProfileId VARCHAR(255),
    Education INTEGER,
    Position INTEGER,
    ProjectsCourses INTEGER, -- BOOLEAN
    ProjectsPersonal INTEGER, -- BOOLEAN
    ProjectsSharedSmall INTEGER, -- BOOLEAN
    ProjectsSharedMedium INTEGER, -- BOOLEAN
    ProjectsSharedLarge INTEGER, -- BOOLEAN
    TeamsSolo INTEGER, -- BOOLEAN
    TeamsSmall INTEGER, -- BOOLEAN
    TeamsMedium INTEGER, -- BOOLEAN
    TeamsLarge INTEGER, -- BOOLEAN
    CodeReviews INTEGER,
    ProgrammingGeneral INTEGER,
    ProgrammingCSharp INTEGER,
    Comment TEXT
);
