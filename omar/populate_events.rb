#!/usr/bin/ruby

require 'json'
require 'logger'
require 'logger/colors'
require 'sqlite3'

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
  db = SQLite3::Database.new 'msr2018_MSVS.db'
  logger.info "SQLite v#{db.get_first_value('SELECT SQLITE_VERSION()')}"
  logger.info "Database connection established. Setting retry to #{timeout}..."
  db.busy_timeout timeout
  logger.info 'Retry timeout established.' if db
  # Read files
  logger.info('Gathering files to process...')
  json_files = Dir['Events-170301/*/*.json']
  logger.info('Files gathered.')
  # Create prepared statement
  query = %{INSERT INTO events (Event_type,IDESessionUUID,KaVEVersion,
    TriggeredAt,TriggeredBy,Duration,ActiveWindow,ActiveDocument,Window,Action,
    Target,Location,TypeOfNavigation,Context2,NumberOfChanges,SizeOfChanges,
    CommandId,Mode,Reason,Scope,Targets,Document,Type,ProposalCollection,
    Selections,TerminatedBy,TerminatedState,ProposalCount,Cancelled,
    IDELifecyclePhase,OpenWindows,OpenDocuments,Actions,Solution,WasAborted,
    Tests,ProfileId,Education,Position,ProjectsCourses,ProjectsPersonal,
    ProjectsSharedSmall,ProjectsSharedMedium,ProjectsSharedLarge,TeamsSolo,
    TeamsSmall,TeamsMedium,TeamsLarge,CodeReviews,ProgrammingGeneral,
    ProgrammingCSharp,Comment) VALUES (:event_type, :sessID, :kaveV, :trigat, :trigby, :duration, :activewin, :activedoc, :win, :action, :target, :location, :navtype, :context2, :changeno, :changesize, :cmdid, :mode, :reason, :scope, :targets, :doc, :type, :propcoll, :sels, :termby, :termstate, :propcnt, :cancelled, :lcphase, :openwin, :opendoc, :actions, :sln, :aborted, :tests, :profid, :edu, :pos, :projcourse, :projpers, :projsmall, :projmed, :projlrg, :teamssolo, :teamssm, :teamsmed, :teamslrg, :coderev, :proggen, :progcs, :comment);}.gsub(/\s+/, ' ').strip
  stmt = db.prepare query
  counter = 0.0
  json_files.each do |jf|
    counter += 1.0
    if counter % 1 == 0
      logger.info "Processing file #{counter.round} of #{json_files.length}."
      logger.info "#{((counter / json_files.length) * 100).round}% complete."
    end
    file = File.read(jf)
    data_hash = JSON.parse(file)
    mapping = {}
    mapping[:event_type] = data_hash.keys.include?('$type') ? data_hash['$type'] : ''
    mapping[:sessID] = data_hash.keys.include?('IDESessionUUID') ? data_hash['IDESessionUUID'] : ''
    mapping[:kaveV] = data_hash.keys.include?('KaVEVersion') ? data_hash['KaVEVersion'] : ''
    mapping[:trigat] = data_hash.keys.include?('TriggeredAt') ? data_hash['TriggeredAt'] : ''
    mapping[:trigby] = data_hash.keys.include?('TriggeredBy') ? data_hash['TriggeredBy'] : ''
    mapping[:duration] = data_hash.keys.include?('Duration') ? data_hash['Duration'] : ''
    mapping[:activewin] = data_hash.keys.include?('ActiveWindow') ? data_hash['ActiveWindow'] : ''
    mapping[:activedoc] = data_hash.keys.include?('ActiveDocument') ? data_hash['ActiveDocument'] : ''
    mapping[:win] = data_hash.keys.include?('Window') ? data_hash['Window'] : ''
    mapping[:action] = data_hash.keys.include?('Action') ? data_hash['Action'] : ''
    mapping[:target] = data_hash.keys.include?('Target') ? data_hash['Target'] : ''
    mapping[:location] = data_hash.keys.include?('Location') ? data_hash['Location'] : ''
    mapping[:navtype] = data_hash.keys.include?('TypeOfNavigation') ? data_hash['TypeOfNavigation'] : ''
    mapping[:context2] = data_hash.keys.include?('Context2') ? data_hash['Context2'] : ''
    mapping[:changeno]= data_hash.keys.include?('NumberOfChanges') ? data_hash['NumberOfChanges'] : ''
    mapping[:changesize] = data_hash.keys.include?('SizeOfChanges') ? data_hash['SizeOfChanges'] : ''
    mapping[:cmdid] = data_hash.keys.include?('CommandId') ? data_hash['CommandId'] : ''
    mapping[:mode] = data_hash.keys.include?('Mode') ? data_hash['Mode'] : ''
    mapping[:reason] = data_hash.keys.include?('Reason') ? data_hash['Reason'] : ''
    mapping[:scope] = data_hash.keys.include?('Scope') ? data_hash['Scope'] : ''
    mapping[:targets] = data_hash.keys.include?('Targets') ? data_hash['Targets'] : ''
    mapping[:doc] = data_hash.keys.include?('Document') ? data_hash['Document'] : ''
    mapping[:type] = data_hash.keys.include?('Type') ? data_hash['Type'] : ''
    mapping[:propcoll] = data_hash.keys.include?('ProposalCollection') ? data_hash['ProposalCollection'] : ''
    mapping[:sels] = data_hash.keys.include?('Selections') ? data_hash['Selections'] : ''
    mapping[:termby] = data_hash.keys.include?('TerminatedBy') ? data_hash['TerminatedBy'] : ''
    mapping[:termstate] = data_hash.keys.include?('TerminatedState') ? data_hash['TerminatedState'] : ''
    mapping[:propcnt] = data_hash.keys.include?('ProposalCount') ? data_hash['ProposalCount'] : ''
    mapping[:cancelled] = data_hash.keys.include?('Cancelled') ? data_hash['Cancelled'] : ''
    mapping[:lcphase] = data_hash.keys.include?('IDELifecyclePhase') ? data_hash['IDELifecyclePhase'] : ''
    mapping[:openwin] = data_hash.keys.include?('OpenWindows') ? data_hash['OpenWindows'] : ''
    mapping[:opendoc] = data_hash.keys.include?('OpenDocuments') ? data_hash['OpenDocuments'] : ''
    mapping[:actions] = data_hash.keys.include?('Actions') ? data_hash['Actions'] : ''
    mapping[:sln] = data_hash.keys.include?('Solution') ? data_hash['Solution'] : ''
    mapping[:aborted] = data_hash.keys.include?('WasAborted') ? data_hash['WasAborted'] : ''
    mapping[:tests] = data_hash.keys.include?('Tests') ? data_hash['Tests'] : ''
    mapping[:profid] = data_hash.keys.include?('ProfileId') ? data_hash['ProfileId'] : ''
    mapping[:edu] = data_hash.keys.include?('Education') ? data_hash['Education'] : ''
    mapping[:pos] = data_hash.keys.include?('Position') ? data_hash['Position'] : ''
    mapping[:projcourse] = data_hash.keys.include?('ProjectsCourses') ? data_hash['ProjectsCourses'] : ''
    mapping[:projpers] = data_hash.keys.include?('ProjectsPersonal') ? data_hash['ProjectsPersonal'] : ''
    mapping[:projsmall] = data_hash.keys.include?('ProjectsSharedSmall') ? data_hash['ProjectsSharedSmall'] : ''
    mapping[:projmed] = data_hash.keys.include?('ProjectsSharedMedium') ? data_hash['ProjectsSharedMedium'] : ''
    mapping[:projlrg] = data_hash.keys.include?('ProjectsSharedLarge') ? data_hash['ProjectsSharedLarge'] : ''
    mapping[:teamssolo] = data_hash.keys.include?('TeamsSolo') ? data_hash['TeamsSolo'] : ''
    mapping[:teamssm] = data_hash.keys.include?('TeamsSmall') ? data_hash['TeamsSmall'] : ''
    mapping[:teamsmed] = data_hash.keys.include?('TeamsMedium') ? data_hash['TeamsMedium'] : ''
    mapping[:teamslrg] = data_hash.keys.include?('TeamsLarge') ? data_hash['TeamsLarge'] : ''
    mapping[:coderev] = data_hash.keys.include?('CodeReviews') ? data_hash['CodeReviews'] : ''
    mapping[:proggen] = data_hash.keys.include?('ProgrammingGeneral') ? data_hash['ProgrammingGeneral'] : ''
    mapping[:progcs] = data_hash.keys.include?('ProgrammingCSharp') ? data_hash['ProgrammingCSharp'] : ''
    mapping[:comment] = data_hash.keys.include?('Comment') ? data_hash['Comment'] : ''
    # Insert rows via prepared statement
    mapping.keys.each do |key|
        stmt.bind_param key, mapping[key]
    end
    logger.info 'Executing insert statement...'
    stmt.execute
    logger.info 'Cleaning statement up...'
    stmt.reset!
  end
  logger.info 'Data insertions complete.'
rescue SQLite3::Exception => e
  logger.fatal 'An exception occurred when accessing the database.'
  logger.error e
ensure
  logger.info 'Closing database connection...'
  sleep 5
  db.close if db
  logger.info 'Database connection closed.'
end
