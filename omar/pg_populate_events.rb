#!/usr/bin/ruby

require 'json'
require 'logger'
require 'logger/colors'
require 'pg'

logger = Logger.new($stdout).tap do |log|
  log.progname = 'populate_events'
end
timeout = 1000

# Read field mapping
logger.info 'Reading field mapping...'
mapping_file = File.read 'event_attribute_mapping.txt'
mapping = JSON.parse mapping_file
logger.info 'Field mapping obtained.'

# Connect to the database
logger.info 'Attempting to connect to the database server...'
stmt = nil
begin
  db = PG.connect(:dbname => 'msr2018')
  logger.info "#{db.exec('SELECT version()').getvalue(0,0)}"
  logger.info 'Database connection established.'
  # Read files
  logger.info('Gathering files to process...')
  json_files = Dir['Events-170301/*/*.json']
  logger.info('Files gathered.')
  # Create prepared statement
  query = %{INSERT INTO events (Event_type,IDESessionUUID,KaVEVersion,
    TriggeredAt,TriggeredBy,Duration,ActiveWindow,ActiveDocument,win,Action,
    Target,Location,TypeOfNavigation,Context2,NumberOfChanges,SizeOfChanges,
    CommandId,Mode,Reason,Scope,Targets,Document,Type,ProposalCollection,
    Selections,TerminatedBy,TerminatedState,ProposalCount,Cancelled,
    IDELifecyclePhase,OpenWindows,OpenDocuments,Actions,Solution,WasAborted,
    Tests,ProfileId,Education,Position,ProjectsCourses,ProjectsPersonal,
    ProjectsSharedSmall,ProjectsSharedMedium,ProjectsSharedLarge,TeamsSolo,
    TeamsSmall,TeamsMedium,TeamsLarge,CodeReviews,ProgrammingGeneral,
    ProgrammingCSharp,Comment) VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,
    $13,$14,$15,$16,$17,$18,$19,$20,$21,$22,$23,$24,$25,$26,$27,$28,$29,$30,
    $31,$32,$33,$34,$35,$36,$37,$38,$39,$40,$41,$42,$43,$44,$45,$46,$47,$48,
    $49,$50,$51,$52);}.gsub(/\s+/, ' ').strip
  db.prepare 'inserts', query
  counter = 0.0
  logger.info 'Starting data insertions...'
  json_files.each do |jf|
    counter += 1.0
    if counter % 500 == 0
      logger.info "Processing file #{counter.round} of #{json_files.length}."
      logger.info "#{(counter / json_files.length).round * 100}% complete."
    end
    file = File.read(jf)
    data_hash = JSON.parse(file)
    params = []
    params << (data_hash.keys.include?('$type') ? data_hash['$type'] : nil)
    params << (data_hash.keys.include?('IDESessionUUID') ? data_hash['IDESessionUUID'] : nil)
    params << (data_hash.keys.include?('KaVEVersion') ? data_hash['KaVEVersion'] : nil)
    params << (data_hash.keys.include?('TriggeredAt') ? data_hash['TriggeredAt'] : nil)
    params << (data_hash.keys.include?('TriggeredBy') ? data_hash['TriggeredBy'] : nil)
    params << (data_hash.keys.include?('Duration') ? data_hash['Duration'] : nil)
    params << (data_hash.keys.include?('ActiveWindow') ? data_hash['ActiveWindow'] : nil)
    params << (data_hash.keys.include?('ActiveDocument') ? data_hash['ActiveDocument'] : nil)
    params << (data_hash.keys.include?('Window') ? data_hash['Window'] : nil)
    params << (data_hash.keys.include?('Action') ? data_hash['Action'] : nil)
    params << (data_hash.keys.include?('Target') ? data_hash['Target'] : nil)
    params << (data_hash.keys.include?('Location') ? data_hash['Location'] : nil)
    params << (data_hash.keys.include?('TypeOfNavigation') ? data_hash['TypeOfNavigation'] : nil)
    params << (data_hash.keys.include?('Context2') ? data_hash['Context2'] : nil)
    params << (data_hash.keys.include?('NumberOfChanges') ? data_hash['NumberOfChanges'] : nil)
    params << (data_hash.keys.include?('SizeOfChanges') ? data_hash['SizeOfChanges'] : nil)
    params << (data_hash.keys.include?('CommandId') ? data_hash['CommandId'] : nil)
    params << (data_hash.keys.include?('Mode') ? data_hash['Mode'] : nil)
    params << (data_hash.keys.include?('Reason') ? data_hash['Reason'] : nil)
    params << (data_hash.keys.include?('Scope') ? data_hash['Scope'] : nil)
    params << (data_hash.keys.include?('Targets') ? data_hash['Targets'] : nil)
    params << (data_hash.keys.include?('Document') ? data_hash['Document'] : nil)
    params << (data_hash.keys.include?('Type') ? data_hash['Type'] : nil)
    params << (data_hash.keys.include?('ProposalCollection') ? data_hash['ProposalCollection'] : nil)
    params << (data_hash.keys.include?('Selections') ? data_hash['Selections'] : nil)
    params << (data_hash.keys.include?('TerminatedBy') ? data_hash['TerminatedBy'] : nil)
    params << (data_hash.keys.include?('TerminatedState') ? data_hash['TerminatedState'] : nil)
    params << (data_hash.keys.include?('ProposalCount') ? data_hash['ProposalCount'] : nil)
    params << (data_hash.keys.include?('Cancelled') ? data_hash['Cancelled'] : nil)
    params << (data_hash.keys.include?('IDELifecyclePhase') ? data_hash['IDELifecyclePhase'] : nil)
    params << (data_hash.keys.include?('OpenWindows') ? data_hash['OpenWindows'] : nil)
    params << (data_hash.keys.include?('OpenDocuments') ? data_hash['OpenDocuments'] : nil)
    params << (data_hash.keys.include?('Actions') ? data_hash['Actions'] : nil)
    params << (data_hash.keys.include?('Solution') ? data_hash['Solution'] : nil)
    params << (data_hash.keys.include?('WasAborted') ? data_hash['WasAborted'] : nil)
    params << (data_hash.keys.include?('Tests') ? data_hash['Tests'] : nil)
    params << (data_hash.keys.include?('ProfileId') ? data_hash['ProfileId'] : nil)
    params << (data_hash.keys.include?('Education') ? data_hash['Education'] : nil)
    params << (data_hash.keys.include?('Position') ? data_hash['Position'] : nil)
    params << (data_hash.keys.include?('ProjectsCourses') ? data_hash['ProjectsCourses'] : nil)
    params << (data_hash.keys.include?('ProjectsPersonal') ? data_hash['ProjectsPersonal'] : nil)
    params << (data_hash.keys.include?('ProjectsSharedSmall') ? data_hash['ProjectsSharedSmall'] : nil)
    params << (data_hash.keys.include?('ProjectsSharedMedium') ? data_hash['ProjectsSharedMedium'] : nil)
    params << (data_hash.keys.include?('ProjectsSharedLarge') ? data_hash['ProjectsSharedLarge'] : nil)
    params << (data_hash.keys.include?('TeamsSolo') ? data_hash['TeamsSolo'] : nil)
    params << (data_hash.keys.include?('TeamsSmall') ? data_hash['TeamsSmall'] : nil)
    params << (data_hash.keys.include?('TeamsMedium') ? data_hash['TeamsMedium'] : nil)
    params << (data_hash.keys.include?('TeamsLarge') ? data_hash['TeamsLarge'] : nil)
    params << (data_hash.keys.include?('CodeReviews') ? data_hash['CodeReviews'] : nil)
    params << (data_hash.keys.include?('ProgrammingGeneral') ? data_hash['ProgrammingGeneral'] : nil)
    params << (data_hash.keys.include?('ProgrammingCSharp') ? data_hash['ProgrammingCSharp'] : nil)
    params << (data_hash.keys.include?('Comment') ? data_hash['Comment'] : nil)
    # Insert rows via prepared statement
    db.exec_prepared('inserts', params)
  end
  logger.info 'Data insertions complete.'
rescue Exception => e
  logger.fatal 'An exception occurred when accessing the database.'
  logger.error e
ensure
  logger.info 'Closing database connection...'
  db.close if db
  logger.info 'Database connection closed.'
end
